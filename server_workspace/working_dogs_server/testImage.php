<?php	
	
	include("smart_resize_image.function.php");
	$file = 'dog_images/dog_image_1.jpg';
	$resizedFile = 'dog_images_thumb/dog_image_1.jpg';
	
	smart_resize_image($file, 50, 50, false, $resizedFile, false, false, 100);
	$data = file_get_contents($file);
	echo $data;


?>