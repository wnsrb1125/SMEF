<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbconnect.php');
        
    
   

    
    $stmt = $con->prepare('select * from testtable3');
    $stmt->execute();
    

    if ($stmt->rowCount() > 0)
    {
        $data = array(); 

        while($row=$stmt->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);

            array_push($data, 
                array('id'=>$id,
                'square1'=>$square1,
                'square2'=>$square2,
                'square3'=>$square3,
                'square4'=>$square4
            ));

            // $fileDir = "data/";
            // $fullPath = $fileDir."/".$id;   
            // $length = filesize($fullPath);

            // header("Content-Type: application/octet-stream");
            // header("Content-Length: $length");
            // header("Content-Disposition: attachment; filename=".iconv('utf-8','euc-kr',$fullPath));
            // header("Content-Transfer-Encoding: binary");

            // $fh = fopen($fullPath, "r");
            // fpassthru($fh);

        }

        header('Content-Type: application/json; charset=utf8');
        $json = json_encode(array("webnautes"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
        echo $json;
    }

?>