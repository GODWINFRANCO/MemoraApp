<?php
include("connection.php");

//creating a query

$stmt = $con->prepare( "SELECT id,caregivername,caregiverphone,caregiveraddress,caregiverfee,caregiverdob,caregivermail,caregiverpass,caregivergender FROM caregiverregister");

//Executting the query

$stmt->execute();

//binding result to the query

$stmt->bind_result($id,$caregivername,$caregiverphone,$caregiveraddress,$caregiverfee,$caregiverdob,$caregivermail,$caregiverpass,$caregivergender);

$p=array();

while($stmt->fetch()){
    $temp=array();
    $temp['id']=$id;
    $temp['caregivername']=$caregivername;
    $temp['caregiverphone']=$caregiverphone;
    $temp['caregiveraddress']=$caregiveraddress;
    $temp['caregiverfee']=$caregiverfee;
    $temp['caregiverdob']=$caregiverdob;
    $temp['caregivermail']=$caregivermail;
    $temp['caregiverpass']=$caregiverpass;
    $temp['caregivergender']=$caregivergender;
   
  
    array_push($p,$temp);

}
echo json_encode($p);