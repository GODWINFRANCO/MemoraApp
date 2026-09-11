
<?php
include("connection.php");

$caregivername =$_POST["caregivername"];
$caregiverphone  =$_POST["caregiverphone"];
$caregiverfees  =$_POST["caregiverfees"];
$caregiverid  =$_POST["caregiverid"];
$patientname =$_POST["patientname"];
$patientid =$_POST["patientid"];
$patientphone =$_POST["patientphone"];
$patientnotes =$_POST["patientnotes"];
$bookingdate =$_POST["bookingdate"];

$q ="INSERT INTO caregiverbooking (caregivername,caregiverphone,caregiverfees,caregiverid,patientid,patientname,patientphone,patientnotes,bookingdate) VALUES ('$caregivername','$caregiverphone','$caregiverfees','$caregiverid','$patientid','$patientname','$patientphone','$patientnotes','$bookingdate')";

$result=mysqli_query($con,$q);
if($result){
    $response["status"]="1";
    $response["message"]=" Booking successful";
}
else{
    $response["status"]="0";
    $response["message"]="Booking failed";
}
echo json_encode($response);
?>

