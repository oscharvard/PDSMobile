package edu.harvard.hul.ois.pds.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.configuration.XMLConfiguration;
import javax.sql.DataSource;

//import edu.harvard.hul.ois.drs2.callservice.FileMetadata;
//import edu.harvard.hul.ois.drs2.callservice.ObjectMetadata;
import edu.harvard.hul.ois.drs2.callservice.ServiceException;
import edu.harvard.hul.ois.drs2.callservice.ServiceResponse;
import edu.harvard.hul.ois.drs2.callservice.ServiceWrapper;
import edu.harvard.hul.ois.drs2.services.dto.DRSFileDTO;
import edu.harvard.hul.ois.drs2.services.dto.DRSObjectDTO;
import edu.harvard.hul.ois.drs2.services.dto.ext.DRSFileDTOExt;
import edu.harvard.hul.ois.drs2.services.dto.ext.DRSObjectDTOExt;
import edu.harvard.hul.ois.pds.PdsConf;
import edu.harvard.hul.ois.pds.ws.PDSWebService;
import edu.harvard.hul.ois.pds.user.PdsUserState;
import edu.harvard.hul.ois.pds.user.PtoMetadata;

public class PdsWebServiceFilter implements Filter {
	
	private DataSource ds;
	private DataSource nrsDs;
	private XMLConfiguration conf;
	private ServiceWrapper drs2Service;
	private final int SELECT_IN_LIMIT = 400; // used when there are tooo many URNs
	private final String OBJ_QUERY = 
			"select filepath,access_flag, modified_date, insertion_date "+
			"from drs_objects o, text_metadata t "+
			"where t.object_id = o.object_id "+
			"and t.descriptor_type = 'PAGEDOBJECT' and o.object_id = ?";
	private final String OWNER_QUERY = 
		"SELECT OWNERS.FULL_NAME as FULL_NAME, URN_MAP.URN as URN "+
		"FROM DRS_OBJECTS, OWNERS, URN_MAP " +
		" WHERE DRS_OBJECTS.OBJECT_ID = ? AND " +
		"DRS_OBJECTS.OWNER_CODE=OWNERS.OWNER_CODE AND "+
		"URN_MAP.OBJECT_ID = DRS_OBJECTS.OBJECT_ID";
	
	private final String NRS_URN_QUERY =
		"select ns.nid, n.nss, l.url " +
        "from name n, lookup l, namespace ns, authority a " +
        " where ns.status = 'A' and a.STATUS='A' " +
        " and n.status = 'A' and l.status = 'A' " +
        " and n.key = l.canonical_fk " +
        " and n.namespace_fk = ns.key " +
        " and a.KEY=n.authority_fk and lower(n.nss) in (";
			
	
	public void init(FilterConfig arg0) throws ServletException {

		try {
			Context initContext = new InitialContext();

			Context envContext = (Context) initContext.lookup("java:/comp/env");
			ds = (DataSource) envContext.lookup("jdbc/DrsDB");
			nrsDs = (DataSource) envContext.lookup("jdbc/NrsDB");
			
			PdsConf pdsConf = (PdsConf) envContext.lookup("bean/PdsConf");
			conf = pdsConf.getConf();
			
			drs2Service = new ServiceWrapper(conf.getString("drs2ServiceURL"),
			        conf.getString("drs2AppKey"),
			        1);
			
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}
	
	public void destroy() {

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		//System.out.println("USER FILTER HIT");
		ArrayList<String> urns = new ArrayList<String>();
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		
		String uri = req.getRequestURI();
		uri = uri.substring(1);
        String[] uriElements = uri.split("/");
        Integer reqObjId = null;
        String reqAction = null;
        Integer reqPage = null;
        if(uriElements.length > 2) {
            //Get the requested action
        	reqAction = uriElements[1];
                //skip all if this is a '/find/global' request
                if ( reqAction.equalsIgnoreCase("find") )
                {
                    if ( uriElements[2].equalsIgnoreCase("global") )
                    {
                     //System.out.println("attempting to do global fts search");
                     req.setAttribute("skip2fts","true");
                     chain.doFilter(req, response);
                     return;
                    }
                }
        	//Get the requested object ID -- required
        	try {reqObjId = new Integer(uriElements[2]);}
            catch (Exception e) { 
            	PDSWebService.printError(req, res, "Invalid Object ID",null);
            	return;
            }
        }
        else {           
                //invalid URL
               PDSWebService.printError(req, res, "Invalid URL",null);
               return;
        }
        
        if(reqObjId==null) {
			//invalid URL, exit with error
        	PDSWebService.printError(req, res, "No Object",null);
        	return;
        }
		
		HttpSession session = req.getSession(true);
		PdsUserState pdsUser = (PdsUserState)session.getAttribute("PdsUser");
		
		if(pdsUser == null) {
			pdsUser = new PdsUserState();
		}
		
		pdsUser.setAction(reqAction); //Set the requested action on the User object
		pdsUser.setPtoID(reqObjId); //Set the requested object id on the User object
		pdsUser.setPage(reqPage); //Set the requested page number on the User object
		PtoMetadata meta = pdsUser.getMeta();
		
		if(meta.getPtoID() == reqObjId && pdsUser.isRAuthorized()) {
			//The request is for the same object as was used previously so
			//metadata does not need to be queried again
		}
		else {
			pdsUser.setRAuthorized(false);//remove authorization in case new object is restricted
			meta.setPtoID(reqObjId);
			
			
			
			
			//look up meta obj info in database
			Connection conn = null;
			PreparedStatement objStmt = null; 
			ResultSet objRs = null;
			PreparedStatement ownerStmt = null; 
			ResultSet ownerRs = null;
			boolean validObject = false;


			try {
				conn = ds.getConnection();
				objStmt = conn.prepareStatement(OBJ_QUERY);
				objStmt.setInt(1,pdsUser.getPtoID());
				objRs = objStmt.executeQuery();
				
				if (!conf.getBoolean("useOnlyDRS2") && objRs.next()) { //If object is found in DRS1 and not migrated  //TODO change OBJ_QUERY to include migrated flag 
					
					meta.setFilepath(objRs.getString("filepath"));
					meta.setAccessFlag(objRs.getString("access_flag"));
					String date;
					//if modified_date is not available use the insertion date
					if(objRs.getDate("modified_date")==null) {
						date = new String(objRs.getDate("insertion_date")+" "+objRs.getTime("insertion_date"));
					}
					else {
						date = new String(objRs.getDate("modified_date")+" "+objRs.getTime("modified_date"));
					}
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					meta.setDBLastModified(formatter.parse(date));
					validObject = true;
					
					//look up owner string
					ownerStmt = conn.prepareStatement(OWNER_QUERY);
					ownerStmt.setInt(1,pdsUser.getPtoID());
					ownerRs = ownerStmt.executeQuery();
					int ct = 0;
					while (ownerRs.next()) {
						if (ct == 0)
							meta.setOwner(ownerRs.getString(1));
						urns.add(ownerRs.getString(2));
						ct++;
					}
					setUrnMappings(meta, urns);

					//TODO display error if object is not returned by query
					objRs.close();
					objRs = null;
					ownerRs.close();
					ownerRs = null;
					objStmt.close();
					objStmt = null;
					ownerStmt.close();
					ownerStmt = null;
					conn.close(); // Return to connection pool
					conn = null; // Make sure we don't close it twice
				}
				//lookup in DRS2
				else if(conf.getBoolean("useDRS2")){
					
					DRSFileDTOExt fmd = null;
					
					//check if ID passed in is an object id
					try {
				        fmd = drs2Service.getFileMetadataByRole(pdsUser.getPtoID().toString(), "OBJECT_DESCRIPTOR",true);
					}
					catch(ServiceException se) {
						//not found
					}
					
					//if not found as an object id, try as a file id
					if (fmd == null) { 			
						fmd = drs2Service.getFileMetadataById(pdsUser.getPtoID().toString(),true);
					}
		
					if(fmd != null) {
						DRSObjectDTOExt objMD = getObjectMD(fmd);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); //TODO fix date format
						if(isValidPTO(fmd,objMD)) {			
							meta.setFilepath(fmd.getFilePath());
							meta.setAccessFlag(objMD.getAccessFlag());
							Date date;
							//if modified_date is not available use the insertion date
							if(fmd.getLastModDate() == null ) {
								date = fmd.getInsertionDate();
							}
							else {
								date = fmd.getLastModDate();
							}
							meta.setDBLastModified(date);
							validObject = true;
						}
					}
					
					//TODO get owner info from drs2
					//TODO get urn from drs2
				}

			} catch (SQLException e) {
				//TODO handle error
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// Always make sure result sets and statements are closed,
				// and the connection is returned to the pool
				if (objRs != null) {
					try {
						objRs.close();
					} catch (SQLException e) {System.err.println(e);}
					objRs = null;
				}
				if (objStmt != null) {
					try {
						objStmt.close();
					} catch (SQLException e) {System.err.println(e);}
					objStmt = null;
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {System.err.println(e);}
					conn = null;
				}

			}
			session.setAttribute("PdsUser",pdsUser);
			if(!validObject) {
				PDSWebService.printError(req, res, "Invalid Object",null);
				return;
			}
		}
		
		//onwards to the ObjectAuthorizationFilter
		chain.doFilter(req, response);
		return;
	}
	private void setUrnMappings(PtoMetadata meta, ArrayList<String> urns) {
		HashMap<String, String> urnMaps = new HashMap<String, String>();
		ResultSet nrsRs = null;
		Connection conn = null;
		Statement stmt = null;
		if (urns.isEmpty()) {
			meta.setUrn("");
		}
		else {
			try {
				conn = nrsDs.getConnection();
				int rounds = urns.size()/SELECT_IN_LIMIT + 1;
				int ct = 0;
				for (int r = 0; r < rounds; r++) {
					StringBuffer statement = new StringBuffer(NRS_URN_QUERY);
					boolean first = true;
					for (;ct< urns.size() && ct < ((r + 1) *SELECT_IN_LIMIT); ct++) {
						String urn = urns.get(ct);

						urn =  urn.substring(6); // drop the "urn-3:"
						if (!first) {
							statement.append(", ");
						}
						else
							first = false;
						statement.append("'").append(urn.toLowerCase()).append("'");
					}
					statement.append(")");


					stmt = conn.createStatement();
					nrsRs = stmt.executeQuery(statement.toString());
					while (nrsRs.next()) {

						String urn = nrsRs.getString(1) + ":" + nrsRs.getString(2);
						String url = nrsRs.getString(3);
						int qinx = url.indexOf('?');
						if (qinx < 0) {
							meta.setUrn(urn);
						} else {
							urnMaps.put(url.substring(qinx), urn);
						}
					}
					if (nrsRs != null)
						nrsRs.close();
					if (stmt != null)
						stmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (nrsRs != null) {
					try {
						nrsRs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					nrsRs = null;
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					stmt = null;
				}

				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					conn = null;
				}
			}
			meta.setUrnMappings(urnMaps);

		}
	}
	private DRSObjectDTOExt getObjectMD(DRSFileDTOExt fmd) throws ServiceException {
		String objID = fmd.getDrsObject().getId().toString();
		DRSObjectDTOExt objMD = drs2Service.getObjectMetadataByID(objID, false);
		return objMD;
	}
	
	private boolean isValidPTO(DRSFileDTOExt fmd, DRSObjectDTOExt objMD) throws ServiceException {
		boolean validRole = fmd.getRoles().contains("OBJECT_DESCRIPTOR");
		boolean validCM = objMD.getContentModel().equals("CMID-4.0");
		if(validRole && validCM) {
			return true;
		}
		else {
			return false;
		}
	}

}
