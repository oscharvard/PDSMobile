#!/usr/bin/perl

use strict;
use File::Basename;
use File::Copy;
use Getopt::Std;
use LWP::UserAgent;
use HTTP::Request;
use HTTP::Response;

##############################################################
# rollover-logs.pl - Rollover log files to archive.
#
# On the first day of the month, rollover the log file to the archive.
# This assumes the logs roll themselves over to a specified 
# naming convention of log_name.YYYYMM (access_log to access_log.200508).
# This script moves those files with last month's date to 
# the archive directory.
##############################################################

# example usages: 
# ./rollover-logs.pl -a /logfiles/ids/prod/archive /home/ids/prod/logs/ids_log
# ./rollover-logs.pl -a /logfiles/lids/prod/archive /home/lids/prod/logs/lids_log /home/lids/prod/logs/lids_error_log

my $usage_message = 
    "usage: rollover-logs.pl -a archive_dir log_file1_full_path [log_file2_full_path ...]";

use vars qw($opt_a);
getopts('a:');

if (!defined($opt_a) || ($opt_a eq "0")) { die $usage_message; }
my $archive = $opt_a;

if (scalar(@ARGV) < 1) {
    print $usage_message;
    exit -1;
}

#Check that the hostname is in cron-list.  If not, then exit.
my $jobHosts = `grep -v '^#' /drsmisc/conf/cron-list | grep "pds_prod_logroll" | cut -f2 -d:`;
my $hostname = `hostname -s`;
chomp($jobHosts);
chomp($hostname);
my $canrun = checkhosts($hostname,$jobHosts);
if($canrun ne "true") {
	die "this script cannot run on $hostname, only on $jobHosts";
}

if (not -e $archive) {
    die "archive_dir: $archive does not exist";
}

my @time = localtime;

# get last month:
my $yyyy = $time[5] + 1900;
my $mm   = $time[4];
$yyyy -= 1 if ($mm == 0);
$mm = 12 if ($mm == 0);
$mm   = sprintf "%02d", $mm;

my $logfile;
my $filename;
my $archfile;
my $outfile;
my $hostname;
chomp($hostname = `hostname -a`);

my $agent = "PingPDS/1.0 ";
my $testurl = "http://$hostname.hul.harvard.edu:9005/pds/view/2573646";

while ($logfile = shift @ARGV) {
    
    $outfile = $logfile . ".$hostname.${yyyy}${mm}";
    $logfile = $logfile . ".${yyyy}${mm}";
    $filename = basename($outfile);
    $archfile = $archive . "/" . $filename;

    if (-e $archfile) {
        die "archive file $archfile already exists";
    }
 
    # if log file does not exist, hit the app to force log4j rollover
    if(not -e $logfile) {
    	print "$logfile does not exist, pinging server to force log4j rollover - ";
		my $ua = new LWP::UserAgent;
		$ua->agent ($agent.$ua->agent);
		my $request = new HTTP::Request GET => $testurl;		
		my $response = $ua->request ($request);
		my $code = $response->code;
		if ($code != 200) {
		   print " FAILED. Could not contact $testurl \n";	   
		}
		else {
			print "OK \n";
			#wait a few seconds for log to rollover
			sleep(10);
		}	
    }
 
 	#check that log file exists before copying it
    if (-e $logfile) {
        copy ($logfile, $archfile);
        if (-e $archfile) {
            unlink ($logfile);
        }
    }
}

sub checkhosts {
	my($hostname, $jobHosts) = @_;
	my @hosts = split('\,', $jobHosts);
	foreach (@hosts) {
		my $host = $_;
		if($host eq $hostname) {
			return "true";
		}
	}
	return "false";
}
