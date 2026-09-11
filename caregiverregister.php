
<?php
include("connection.php");

$caregivername =$_POST["caregivername"];
$caregivermail =$_POST["caregivermail"];
$caregiverphone  =$_POST["caregiverphone"];
$caregiveraddress  =$_POST["caregiveraddress"];
$caregiverfee  =$_POST["caregiverfee"];
$caregiverpass =$_POST["caregiverpass"];
$caregivergender =$_POST["caregivergender"];
$caregiverdob =$_POST["caregiverdob"];

$q ="INSERT INTO caregiverregister (caregivername,caregiverphone,caregiveraddress,caregiverfee,caregiverdob,caregivermail,caregiverpass,caregivergender) VALUES ('$caregivername','$caregiverphone','$caregiveraddress','$caregiverfee','$caregiverdob','$caregivermail','$caregiverpass','$caregivergender')";

$result=mysqli_query($con,$q);
if($result){
    $response["status"]="1";
    $response["message"]=" Registration successful";
}
else{
    $response["status"]="0";
    $response["message"]="Registration failed";
}
echo json_encode($response);
?>

