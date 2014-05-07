<?php
	// User Validation
	$validID = '39Dkf93ja91dvMNa02ns1d4N9';
	$ID = $_POST['validation'];
	if ($ID != $validID)
	{
		exit('invalid_id');	
	}
	// Connect to server
	$con = new mysqli("mysql.freehostingnoads.net","u239530328_user","cis35o","u239530328_serv", 3306);
	
	$JSONMessage = $_POST['jsonMessage'];
	// Associative array of id number to version number from device
	$info = json_decode($JSONMessage, true);
	
	foreach($info as $catTableName => $wrapper)
	{
		$dogID = $wrapper["dogID"];
		$catKey = $wrapper["categoryKey"];
		$tableRows = $wrapper["tableRows"];
		/*
		 * Check the skills table to see if this category has been added
		 * If it hasn't, add it and create a table for it
		 */
		$skillsTableName = "skills_table_".strval($dogID);
		// Check to see if category key present in skills table
		$sql = "SELECT category_name FROM ".$skillsTableName." WHERE category_name=?";
		$stmt = $con->prepare($sql);
		$stmt->bind_param('s', $catKey);
		$stmt->execute();
		$stmt->bind_result($fetchedUserName);
		$catKeyPresent = false;
		if ($stmt->fetch())
		{
			$catKeyPresent = true;
		}
		// If it's not present
		if (!$catKeyPresent)
		{
			// Add entry to skills table
			$stmt = $con->prepare('INSERT INTO '.$skillsTableName.' (category_name) VALUES (?)');
			$stmt->bind_param('s', $catKey); // Will go back to add if planned after adding individual entries
			$stmt->execute();
			// Create new category table
			$stmt = $con->prepare("CREATE TABLE ".$catTableName."(session_date VARCHAR(50), plan VARCHAR(300), trials_result VARCHAR(50), trainer_username VARCHAR(50))");
			$stmt->execute();
		}
		// Increment the version number
		else 
		{
			$con = new mysqli("mysql.freehostingnoads.net","u239530328_user","cis35o","u239530328_serv", 3306);
			$stmt = $con->prepare("UPDATE ".$skillsTableName." SET version_number = version_number + 1");
			$stmt->execute();
		
		}
		$con = new mysqli("mysql.freehostingnoads.net","u239530328_user","cis35o","u239530328_serv", 3306);
		// Set planned to false and delete any old plans
		$stmt = $con->prepare("UPDATE ".$skillsTableName." SET planned='1' WHERE category_name=?");
		$stmt->bind_param("s", $catKey);
		$stmt->execute();
		
		$stmt = $con->prepare("DELETE FROM ".$catTableName." WHERE trials_result='EMPTY'");
		$stmt->execute();
		
		// Iterate over each row and add to catTable
		foreach($tableRows as $row)
		{
			$sessionDate = $row["session_date"];
			$plan = $row["plan"];
			$trialsResult = $row["trials_result"];
			$userName = $row["trainer_username"];
			$sql = "INSERT INTO ".$catTableName."(session_date, plan, trials_result, trainer_username) VALUES (?, ?, ?, ?)";
			$stmt = $con->prepare($sql);
			$stmt->bind_param('ssss', $sessionDate, $plan, $trialsResult, $userName);
			$stmt->execute();
			
			// This indicates that the category should be planned
			if(strcmp($plan, "EMPTY"))
			{
				$stmt = $con->prepare("UPDATE ".$skillsTableName." SET planned='0' WHERE category_name=?");
				$stmt->bind_param("s", $catKey);
				$stmt->execute();
			}
		}		
	}
	exit('success');
?>