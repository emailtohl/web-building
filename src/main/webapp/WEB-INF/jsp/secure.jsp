<!DOCTYPE html>
<html>
    <head>
        <title>Secure Page</title>
    </head>
    <body>
    	Hello <security:authentication property="principal.username" />!
    	<security:authorize access="isAuthenticated() and principal.username=='emailtohl@163.com'">
    		<p>Administrator</p>
    	</security:authorize>
    	
        <br />
        
        &lt;security:authentication&gt; JSP tag:
        <p>
        authorities: 
        	A collection of GrantedAuthority objects that represent the privileges granted to the user
        </p>
        <p>
        credentials: 
        	The credentials that were used to verify the principal (commonly, this is the user’s password)
        </p>
        <p>
        details: 
        	Additional information about the authentication (IP address, certificate serial number, session ID, and so on)
        </p>
        <p>
        principal: 
        	The user's principal
        	${sessionInformation.sessionId}
	        <h6>allPrincipals</h6>
	        <label>${allPrincipalsJson}</label>
	        <ol>
	        	<c:forEach var="x" items="${authorities}">
	        		<li>${x}</li>
	        	</c:forEach>
	        </ol>
        </p>
        
        <br />
    </body>
</html>