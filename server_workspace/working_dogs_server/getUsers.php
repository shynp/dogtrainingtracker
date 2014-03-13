<?php
	// User Validation
	$validID = '39Dkf93ja91dvMNa02ns1d4N9';
	$ID = $_POST['validation'];
	if ($ID != $validID)
	{
		exit('invalid_id');	
	}
	// Connect to server
	$users = array();
	// Query for users
	$con = new mysqli("mysql.freehostingnoads.net","u239530328_user","cis35o","u239530328_serv", 3306);
	$result = $con->query("SELECT * FROM users");
	$index = 0;
	if ($result->num_rows > 0)
	{
		while($row = $result->fetch_assoc())
		{
			$userAssoc = array(
				'id' => $row['id'],
				'name' => $row['name'],
				'username' => $row['username'],
				'password' => $row['password'],
				'email' => $row['email'],
				'phone' => $row['phone']
			);
			$users[$index] = $userAssoc;
			++$index;
		}
	}
	// Send JSON back to caller
	exit(json_encode($users));
?>