package edu.harvard.hul.ois.pds.filters;

import java.io.IOException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.configuration.XMLConfiguration;
import edu.harvard.hul.ois.access.HulCookie;
import edu.harvard.hul.ois.pds.PdsConf;
import edu.harvard.hul.ois.pds.ws.PDSWebService;
import edu.harvard.hul.ois.pds.exceptions.AccessDeniedException;
import edu.harvard.hul.ois.pds.exceptions.NeedCookieException;
import edu.harvard.hul.ois.pds.user.PdsUserState;
import edu.harvard.hul.ois.pds.user.PtoMetadata;

public class WebServiceAuthorizationFilter implements Filter {
	
    private XMLConfiguration conf = null;
    private String accessUrl;
	
	public void init(FilterConfig config) throws ServletException {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			PdsConf pdsConf = (PdsConf) envContext.lookup("bean/PdsConf");
			conf = pdsConf.getConf();
		} catch (NamingException e) {
			e.printStackTrace();
		}
        accessUrl = conf.getString("AccessUrl");
	}
	
	public void destroy() {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		//System.out.println("OBJ AUTH FILTER HIT");
		
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;

                //if this is a fts request, skip this entirely
                String skip2fts  = (String)req.getAttribute("skip2fts");
                if ("true".equalsIgnoreCase(skip2fts))
                {
                    //System.out.println("skipping global fts search");
                    chain.doFilter(req,res);
                    return;
                }
		
		HttpSession session = req.getSession(false);
		//To get here the request will first have gone through the PdsUserFilter
		//so the PdsUser object must exist in the session
		PdsUserState pdsUser = (PdsUserState)session.getAttribute("PdsUser");
		
		if(pdsUser == null) {
			//something is wrong, this shouldn't be possible because request should have
			// passed through the PdsUserFilter first.
			PDSWebService.printError(req, res, "PDS WS Session Error",null);
			return;
		}
		/*else if(pdsUser.isRAuthorized()) { //User is already authorized for this object
			//onward to the LoggingFilter
			chain.doFilter(req,res);
			return;
		}*/
		
		PtoMetadata meta = pdsUser.getMeta();
		String access = meta.getAccessFlag();

		try {
			if(canAccess(access, req)) {
				pdsUser.setRAuthorized(true);
				//onward to the LoggingFilter
				chain.doFilter(req,res);
				return;
			}
		} catch(NeedCookieException nce) {
			//go to access service URL 
            String resUrl = accessUrl + meta.getPtoID();                        
            res.sendRedirect(resUrl);
            return;
        }
        catch(AccessDeniedException ade) {
        	//show to error page
        	PDSWebService.printError(req, res, ade.getMessage(),null);
            return;
        }  
		

	}
	
	private boolean canAccess(String access, HttpServletRequest req) throws NeedCookieException, AccessDeniedException {
	    /****
	     * CHECK FOR P 
	     ****/
	    //check for "P" here for efficiency.  If it passes return immediately
	    if(access.equalsIgnoreCase("P")) {
	    	//public file, let them through
	    	return true;
	    }
            else if(access.equalsIgnoreCase("R")) {
                // DO NOT SHOW RESTRICTED OBJECTS AT ALL; THIS IS A PUBLIC SERVICE ONLY ATM
                throw new AccessDeniedException("The object you have requested is restricted.");
            }
	    /****
	     * CHECK FOR R 
	     ****/
	    /* if(access.equalsIgnoreCase("R")) {
		    HulCookie hulcookie = null;
		    //Get the hulcookie if it exists
		    Cookie cookies[] = req.getCookies();
		    if ((cookies != null) &&
		        (HulCookie.isHulCookie(cookies))) {
		    	 hulcookie = new HulCookie(cookies);
		    } 
		    
	    	if(hulcookie != null) {
	    		//they qualify to view the restricted pto so let them through;
		    	return true;
	    	}
	    	else {
	    		throw new NeedCookieException("redirect to hulaccess");
	    	}			
	    }    */
	    /****
	     * CHECK FOR N and B
	     ****/
	    else if(access.equalsIgnoreCase("N") || access.equalsIgnoreCase("B")) {
           	//Access is denied
            throw new AccessDeniedException("The object you have requested is not intended for delivery");
	    }
	    return false;
	}



}
