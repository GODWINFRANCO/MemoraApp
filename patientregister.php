
<?php
include("connection.php");

$patientname =$_POST["patientname"];
$patientmail =$_POST["patientmail"];
$patientphone  =$_POST["patientphone"];
$patientaddress  =$_POST["patientaddress"];
$patientpass =$_POST["patientpass"];
$patientgender =$_POST["patientgender"];
$patientdob =$_POST["patientdob"];

$q ="INSERT INTO patientregister (patientname,patientphone,patientaddress,patientdob,patientmail,patientpass,patientgender) VALUES ('$patientname','$patientphone','$patientaddress','$patientdob','$patientmail','$patientpass','$patientgender')";

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

