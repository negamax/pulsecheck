# pulsecheck

# Introduction

Pulse check is a simple utility to check websites for errors and reliability. Current implementation provides checking for 
javascript errors, http status checks and ssl cert expiration. But new checks can be added easily. Original intention and
use case is to monitor internet properties but any protocol or checks can be added easily

# Some Use cases

* Error detection
* Reliability monitoring
* Alerts

# Tech

1. Read and write from AWS Dynamodb
2. Easy to add new checkers (in absence of better name)
3. Uses Spring framework and other best practices
4. Comes with a reports portal (coming really soon)
5. Following checkers are included
    * Javascript errors
    * Http status 
    * Https cert expiration 
   
# REST Endpoints (JSON Output) (level 0 REST)
      
* /sites - list of sites being monitored with each entry containing flags for errors
* /sslcertconnectionerrors - list of sites that failed on ssl connection
* /sslcertexpirationerrors - list of sites having their ssl cert expiring within a said time (default 6 months)  
* /javascripterrors - list of sites with corresponding js errors captured in last two hours (default)
* /httperrors - list of sites which generated http connection error in last one hour (default)
      