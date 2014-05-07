<?php
	// User Validation
	$validID = '39Dkf93ja91dvMNa02ns1d4N9';
	$ID = $_POST['validation'];
	if ($ID != $validID)
	{
		exit('invalid_id');	
	}
	$email = $_POST['email'];
	
	$con = new mysqli("mysql.freehostingnoads.net","u239530328_user","cis35o","u239530328_serv", 3306);
	$stmt = $con->prepare("SELECT username, password FROM users WHERE email = ?");
	$stmt->bind_param('s', $email);
	$stmt->execute();
	$stmt->bind_result($username, $password);
	$emailContent = "";
	while($stmt->fetch())
	{
		$info = 'Username: '.$username." \r\n".'Password: '.$password;
		$emailContent = $emailContent.$info;
	}	
	if ($emailContent == '')
	{
		exit('invalid_email');
	}
	// Send recovery info to email
	$switch = mail($email, 'Account Recovery - PVWDC Training Tracker',$emailContent);
	exit('success');
?>