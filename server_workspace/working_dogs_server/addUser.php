<?php
	
	// Retrieve Post variables
	$name = $_POST['name'];
	$userName = $_POST['username'];
	$password = $_POST['password'];
	$email = $_POST['email'];
	$phone = $_POST['phone'];
	
	// Connect to server
	$con = mysqli_connect("mysql.freehostingnoads.net","u239530328_user","cis35o","u239530328_serv", 3306);
	
	// Check to see if username has been taken
	$stmt = $con->prepare("SELECT username FROM users WHERE username = ?");
	$stmt->bind_param('s', $userName);
	$stmt->execute();
	
	$stmt->bind_result($fetchedUserName);
	
	if ($stmt->fetch())
	{
		exit('invalid_username');
	}
	// If it hasn't been taken then we can add the new user information
	$stmt = $con->prepare('INSERT INTO users (name, username, password, email, phone) VALUES (?, ?, ?, ?, ?)');
	$stmt->bind_param('sssss', $name, $userName, $password, $email, $phone);
	$stmt->execute();
	exit('success');
?>