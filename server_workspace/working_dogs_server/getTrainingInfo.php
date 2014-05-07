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
	$info = json_decode($JSONMessage, true);
	
	// Query for dogs
	$con = new mysqli("mysql.freehostingnoads.net","u239530328_user","cis35o","u239530328_serv", 3306);
	$result = $con->query("SELECT * FROM dogs");


	$mapping = array();
	// Iterate over the dog table
	while($row = $result->fetch_assoc())
	{
		if ($row['active'] == false)
		{
			continue;
		}
		
		$id = $row['id'];
		$dogInfo = $info[$id];
		$skillsTableName = $row['skills_table_name'];
		$allAssocs = getAllAssocsForDog($skillsTableName, $id, $dogInfo, $con);
		if (count($allAssocs) > 0)
		{
			$mapping[strval($id)] = $allAssocs;
		}
	}
	exit(json_encode($mapping));
	//====================================================================================================
	/*
	 * Mapping of category to rows where rows given by getRowsForCategoryById method
	 */
	function getAllAssocsForDog($skillsTableName, $id, $dogInfo, $con)
	{
		$assocResult = array();
		
		$skillResult = $con->query("SELECT * FROM ".$skillsTableName);	
		if ($skillResult == false) 
		{
			return $assocResult;
		}
		// Iterate over the skill table
		while ($skillRow = $skillResult->fetch_assoc())
		{
			$catKey = $skillRow['category_name'];
			$versionServer = $skillRow['version_number'];
			if (array_key_exists($catKey, $dogInfo))
			{
				$versionDevice = $dogInfo[$catKey];
			}
			else 
			{
				$versionDevice = -1;
			}
			// Add info for this table
			if ($versionServer > $versionDevice)
			{
				$wrapper = array();
				$wrapper['version_number'] = $versionServer;
				$wrapper['rows'] = getRowsForCategoryById($catKey, $id, $con);
				$assocResult[$catKey] = $wrapper;
			}
		}
		return $assocResult;
	}
	/*
	 * Array or rows where row is mapping of column name to value
	 */
	function getRowsForCategoryById($catKey, $id, $con)
	{
		$catArray = array();
		$catTableName = $catKey."_".strval($id);
		$result = $con->query("SELECT * FROM ".$catTableName);
		
		$catIndex = 0;
		while ($row = $result->fetch_assoc())
		{
			$entry = array();
			$entry['session_date'] = $row['session_date'];
			$entry['plan'] = $row['plan'];
			$entry['trials_result'] = $row['trials_result'];
			$entry['trainer_username'] = $row['trainer_username'];
			$catArray[$catIndex] = $entry; 
			$catIndex = $catIndex + 1;
		}
		return $catArray;
	}

?>