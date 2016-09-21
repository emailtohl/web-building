<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>更改密码</title>
	</head>
	<body>
		<form action="${url}" method="post">
			<input type="hidden" name="email" value="${email}">
			<input type="hidden" name="_csrf" value="${_csrf}">
			<p>
				<input type="password" placeholder="Password" name="password">
			</p>
			<p>
				<input type="password" placeholder="Retype password" name="retype-password">
			</p>
		</form>
	</body>
	<script>
		document.querySelector('form').onsubmit = function() {
			var password, retypePassword;
			password = document.querySelector('input[name="password"]').value;
			retypePassword = document.querySelector('input[name="retype-password"]').value;
			if (!(password && retypePassword)) {
				alert('输入框不能为空，且密码不能小于6位');
				return false;
			}
			if (password != retypePassword) {
				alert('密码不一致');
				return false;
			}
		};
		
	</script>
</html>