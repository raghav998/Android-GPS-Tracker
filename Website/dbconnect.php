<?php
error_reporting( E_ALL & ~E_DEPRECATED & ~E_NOTICE );
if(!mysql_connect("127.0.0.1","root",""))
{
	die('oops connection problem ! --> '.mysql_error());
}
if(!mysql_select_db("gps"))
{
	die('oops database selection problem ! --> '.mysql_error());
}

?>