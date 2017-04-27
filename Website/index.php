<?php
session_start();
include_once 'dbconnect.php';

if(isset($_SESSION['user'])!="")
{
	header("Location: home.php");
}

if(isset($_POST['btn-login']))
{
	$email = mysql_real_escape_string($_POST['email']);
	$upass = mysql_real_escape_string($_POST['pass']);
	$colour = mysql_real_escape_string($_POST['colour']);
	
	$email = trim($email);
	$upass = trim($upass);
	$colour = trim($colour);
	
	$res=mysql_query("SELECT user_id, colour, user_name, user_pass FROM users WHERE user_email='$email'");
	$row=mysql_fetch_array($res);
	
	$count = mysql_num_rows($res); // if uname/pass correct it returns must be 1 row
	
	if($count == 1 && $row['user_pass']==md5($upass))
	{
		$_SESSION['user'] = $row['user_id'];
		header("Location: home.php");
	}
	else
	{
		?>
        <script>alert('Username / Password Seems Wrong !');</script>
        <?php
	}
	
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Android GPS Finder - Login</title>
<link rel="stylesheet" href="css/style.css" type="text/css" />
<link rel="stylesheet" href="css/set1.css" type="text/css" />
<link rel="stylesheet" href="css/normalize.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="fonts/font-awesome-4.2.0/css/font-awesome.min.css" />
</head>
<body style = "background-color: rgba(0,0,0,0.85)">
	<center>
		<form method="post">
			<section class="content" id = "login-form">
					<span class="input input--hoshi">
						<input class="input__field input__field--hoshi" id="input-5" type="text" name="email" placeholder="Your Email" required />
						<label class="input__label input__label--hoshi input__label--hoshi-color-2" for="input-5">
						</label>
					</span><br>
					<span class="input input--hoshi">
						<input class="input__field input__field--hoshi" id="input-6" type="password" name="pass" placeholder="Your Password" required />
						<label class="input__label input__label--hoshi input__label--hoshi-color-2" for="input-6">
						</label>
					</span>
			</section>
		<button style = "margin-top: -35px" type = "submit" name="btn-login" class="btn btn-2 btn-2a">Sign In</button>
		</form>
		<a href="register.php">Sign Up Here</a>
	</center>
</body>
</html>