
<?php
include("connection.php");

$patientmail=$_POST["patientmail"];
$patientpass=$_POST["patientpass"];
$query="SELECT * FROM patientregister WHERE patientmail='$patientmail' && patientpass='$patientpass'";
$result=mysqli_query($con,$query);
$row=mysqli_fetch_row($result);
if(mysqli_num_rows($result)>0)
{
    $response["status"]="1";
    $response["message"]="Login Successful";
    $response["id"]=$row[0];
    $response["patientname"]=$row[1];
    $response["patientphone"]=$row[2];
    $response["patientaddress"]=$row[3];
    $response["patientdob"]=$row[4];
    $response["patientmail"]=$row[5];
    $response["patientpass"]=$row[6];
    $response["patientgender"]=$row[7];
    $response["patientpfp"]=$row[8];

    

 

}
else
{
    $response["status"]="0";
    $response["message"]="Login failed";
    $response["id"]="";
    $response["patientname"]="";
    $response["patientphone"]="";
    $response["patientaddress"]="";;
    $response["patientdob"]="";
    $response["patientmail"]="";
    $response["patientpass"]="";
    $response["patientgender"]="";
    $response["patientpfp"]="";

}
echo json_encode($response);
?>