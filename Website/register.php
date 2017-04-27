<?php
session_start();
if(isset($_SESSION['user'])!="")
{
	header("Location: home.php");
}
include_once 'dbconnect.php';

if(isset($_POST['btn-signup']))
{
	$uname = mysql_real_escape_string($_POST['uname']);
	$email = mysql_real_escape_string($_POST['email']);
	$upass = md5(mysql_real_escape_string($_POST['pass']));
	$colour = mysql_real_escape_string($_POST['colour']);
	
	$uname = trim($uname);
	$email = trim($email);
	$upass = trim($upass);
	$colour = trim($colour);
	
	// email exist or not
	$query = "SELECT user_email FROM users WHERE user_email='$email'";
	$result = mysql_query($query);
	
	$count = mysql_num_rows($result); // if email not found then register
	
	if($count == 0){
		
		if(mysql_query("INSERT INTO users(user_name,user_email,user_pass,colour) VALUES('$uname','$email','$upass','$colour')"))
		{
			?>
			<script>alert('successfully registered ');</script>
			<?php
		}
		else
		{
			?>
			<script>alert('error while registering you...');</script>
			<?php
		}		
	}
	else{
			?>
			<script>alert('Sorry Email ID already taken ...');</script>
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
			<section class="content" id = "login-form" style = "margin-top: -100px">
					<span class="input input--hoshi">
						<input class="input__field input__field--hoshi" id="input-5" type="text" name="uname" placeholder="User name" required />
						<label class="input__label input__label--hoshi input__label--hoshi-color-2" for="input-5">
						</label>
					</span><br>
					<span class="input input--hoshi">
						<input class="input__field input__field--hoshi" id="input-6" type="email" name="email" placeholder="Email Address" required />
						<label class="input__label input__label--hoshi input__label--hoshi-color-2" for="input-6">
						</label>
					</span><br>
					<span class="input input--hoshi">
						<input class="input__field input__field--hoshi" id="input-7" type="password" name="pass" placeholder="Password" required />
						<label class="input__label input__label--hoshi input__label--hoshi-color-2" for="input-7">
						</label>
					</span>
			</section>
			<select name="colour">
				<option value="red">Red</option>
				<option value="blue">Blue</option>
				<option value="purple">Purple</option>
				<option value="yellow">Yellow</option>
				<option value="green">Green</option>
			</select>
		<button style = "margin-top: -35px" type = "submit" name="btn-signup" class="btn btn-2 btn-2a">Sign Up</button>
		</form>
		<a href="index.php">Sign In Here</a>
	</center>
</body>
</html>