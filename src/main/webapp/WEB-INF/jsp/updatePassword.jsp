<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>AdminLTE 2 | Starter</title>
  <!-- Tell the browser to be responsive to screen width -->
  <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
  <!-- Bootstrap 3.3.6 -->
  <link rel="stylesheet" href="lib/bootstrap/css/bootstrap.min.css">
  <!-- Font Awesome -->
  <link rel="stylesheet" href="lib/fonts/font-awesome.min.css">
  <!-- Ionicons -->
  <link rel="stylesheet" href="lib/fonts/ionicons.min.css">
  <!-- jqueryui -->
  <link rel="stylesheet" href="lib/jqueryui/jquery-ui.min.css">
  <link rel="stylesheet" href="lib/bootstrap-jquery-ui/jquery-ui-1.10.0.custom.css">
  
  <!-- Theme style -->
  <link rel="stylesheet" href="lib/AdminLTE/css/AdminLTE.min.css">
  <!-- AdminLTE Skins. We have chosen the skin-blue for this starter
        page. However, you can choose any other skin. Make sure you
        apply the skin class to the body tag so the changes take effect.
  -->
  <link rel="stylesheet" href="lib/AdminLTE/css/skins/skin-blue.min.css">

  <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
  <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
  <!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
  <![endif]-->
</head>
<body class="hold-transition register-page">
<div class="register-box">
  <div class="register-logo">
    <a href="../../index2.html"><b>Admin</b>LTE</a>
  </div>

  <div class="register-box-body">
    <p class="login-box-msg">Update Password</p>
    <c:if test="${param.containsKey('error')}">
     <div class="callout callout-danger">
       <h4>Warning!</h4>
       <p>Update password failed. <c:out value="${param.error}"></c:out>.</p>
     </div>
        <!-- <b style="color: red;">Login failed. Please try again.</b><br /><br /> -->
    </c:if>
    <form action="updatePassword" method="post">
      <div class="form-group has-feedback">
        <input readonly="readonly" type="email" class="form-control" name="email" value="${email}">
        <input type="hidden" class="form-control" name="token" value="${token}">
        <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
      </div>
      <div class="form-group has-feedback">
        <input type="password" class="form-control" placeholder="new Password" name="password">
        <span class="glyphicon glyphicon-lock form-control-feedback"></span>
      </div>
      <div class="form-group has-feedback">
        <input type="password" class="form-control" placeholder="Retype password" name="retype-password">
        <span class="glyphicon glyphicon-log-in form-control-feedback"></span>
      </div>
      <!-- 启用CSRF功能时，spring security将会把token写入此表单中 -->
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <div class="row">
        <div class="col-xs-8">
          <div class="checkbox icheck">
            <label>
              <input type="checkbox"> I agree to the <a href="#">terms</a>
            </label>
          </div>
        </div>
        <!-- /.col -->
        <div class="col-xs-4">
          <button type="submit" class="btn btn-primary btn-block btn-flat">Update</button>
        </div>
        <!-- /.col -->
      </div>
    </form>

    <div class="social-auth-links text-center">
      <p>- OR -</p>
      <a href="#" class="btn btn-block btn-social btn-facebook btn-flat"><i class="fa fa-facebook"></i> Sign up using
        Facebook</a>
      <a href="#" class="btn btn-block btn-social btn-google btn-flat"><i class="fa fa-google-plus"></i> Sign up using
        Google+</a>
    </div>

    <a href="login" class="text-center">I already have a membership</a>
  </div>
  <!-- /.form-box -->
</div>
<!-- /.register-box -->


<div class="modal modal-danger">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">×</span></button>
        <h4 class="modal-title">输入无效</h4>
      </div>
      <div class="modal-body">
        <p id="content"></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-outline pull-left" data-dismiss="modal">Close</button>
      </div>
    </div>
    <!-- /.modal-content -->
  </div>
  <!-- /.modal-dialog -->
</div>

<!-- jQuery 2.2.3 -->
<script src="lib/jquery/jquery-2.2.3.min.js"></script>
<script src="lib/jqueryui/jquery-ui.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="lib/bootstrap/js/bootstrap.min.js"></script>
<!-- iCheck -->
<script src="lib/iCheck/icheck.min.js"></script>
<script>
  $(function () {
    $('input').iCheck({
      checkboxClass: 'icheckbox_square-blue',
      radioClass: 'iradio_square-blue',
      increaseArea: '20%' // optional
    });
  });
  
  function tip(content) {
	  var div = $('div.modal');
	  div.find('#content').text(content);
	  div.modal('show');                // initializes and invokes show immediately
  }
  
  $('form').on('submit', function(e) {
	  var email, password, retypePassword;
	  email = $('input[name="email"]').val();
	  password = $('input[name="password"]').val();
	  retypePassword = $('input[name="retype-password"]').val();
	  //e.preventDefault();
	  if (!(email && password && retypePassword)) {
		  tip('输入框不能为空');
		  return false;
	  }
	  if (password != retypePassword) {
		  tip('密码不一致');
		  return false;
	  }
  });
  
  
</script>
</body>
</html>