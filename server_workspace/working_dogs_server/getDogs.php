<?php
	include('SimpleImage.php');
	// User Validation
	$validID = '39Dkf93ja91dvMNa02ns1d4N9';
	$ID = $_POST['validation'];
	if ($ID != $validID)
	{
		exit('invalid_id');	
	}
	$idToVersionJSONString = $_POST['idToVersionNumber'];
	// Associative array of id number to version number from device
	$idToVersion = json_decode($idToVersionJSONString, true);
	// Connect to server
	$dogs = array();
	$ids = array(); // List of id's to keep
	// Query for dogs
	$con = new mysqli("mysql.freehostingnoads.net","u239530328_user","cis35o","u239530328_serv", 3306);
	$result = $con->query("SELECT * FROM dogs");
	$index = 0;
	$idIndex = 0;
	if ($result->num_rows > 0)
	{
		while($row = $result->fetch_assoc())
		{
			if ($row['active'] == false)
			{
				continue;
			}
			$ids[$idIndex] = $row['id'];
			++$idIndex;
			
			$imgPath = "dog_images_thumb".DIRECTORY_SEPARATOR."dog_image_".strval($row['id']).".jpg";
			$data = file_get_contents($imgPath);
			$encImg = base64_encode($data);
			
			// Get the version number the device has
			$deviceVersionNumber = isset($idToVersion[$row['id']]) ? $idToVersion[$row['id']] : -1;
			// If device version number greater or equal to server value then device already has latest
			// entry so can continue
			if ($deviceVersionNumber == $row['version_number'])
			{
				continue;
			}
			
			$userAssoc = array(
				'id' => $row['id'],
				'name' => $row['name'],
				'skills_table_name' => $row['skills_table_name'],
				'birth_date' => $row['birth_date'],
				'breed' => $row['breed'],
				'service_type' => $row['service_type'],
				'image' => $encImg,
				'version_number' => $row['version_number']
			);
			$dogs[$index] = $userAssoc;
			++$index;
		}
	}
	$result = array(
	'dogs' => $dogs,
	'ids' => $ids
	);
	// Send JSON back to caller
	exit(json_encode($result));
?>