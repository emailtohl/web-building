<!DOCTYPE html>
<html>
    <head>
        <title>Admin Page</title>
    </head>
    <body>
        This is an admin-only page.<br />
        <br />
        <form method="post" action="<c:url value="/logout" />" name="logoutForm">
            <a href="javascript:void 0;"
               onclick="document.logoutForm.submit();">Log Out</a>
        </form>
    </body>
</html>
