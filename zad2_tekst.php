<?php

$data = array();
$data_file = fopen("text_data.json", w);

$data["tekst"] = $_POST['ledtext']; 



$data_json = json_encode($data);
fwrite($data_file, $data_json);
fclose($data_file);

shell_exec('sudo ./leds_text.py');

?>