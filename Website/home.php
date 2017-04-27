<?php
session_start();
include_once 'dbconnect.php';

if(!isset($_SESSION['user']))
{
	header("Location: index.php");
}
$res=mysql_query("SELECT * FROM users WHERE user_id=".$_SESSION['user']);
$userRow=mysql_fetch_array($res);
?>

<!DOCTYPE html>
<html>
    <head>
        <title>Android GPS</title>
        
        <!-- Bootstrap Core CSS -->
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">

        <!-- Custom CSS -->
        <link href="css/the-big-picture.css" rel="stylesheet">

        <link href="css/component.css" rel="stylesheet">

        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/animate.css/3.2.0/animate.min.css">

        <script src="http://ajax.aspnetcdn.com/ajax/jQuery/jquery-1.12.0.min.js"></script>

        <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?sensor=false"></script>

        <script src="js/classie.js"></script>
        <script src="js/modalEffects.js"></script>

        <style type="text/css">
           
            html, body, .container-fluid, #map {
                padding: 0;
                margin: 0;
            }
            /* Set a size for our map container, the Google Map will take up 100% of this container */
            #map {
                height: 1080px;
            }

            #main-content {
                padding: 0;
            }

            .navbar {
                opacity: 0.85;
            }
            table {
                width: 100%;
            }
            th {
                text-align: center;
            }
            th, td {
                border-bottom: 1px solid #ddd;
            }
        </style>
        
        <!-- 
            You need to include this script tag on any page that has a Google Map.

            The following script tag will work when opening this example locally on your computer.
            But if you use this on a localhost server or a live website you will need to include an API key. 
            Sign up for one here (it's free for small usage): 
                https://developers.google.com/maps/documentation/javascript/tutorial#api_key

            After you sign up, use the following script tag with YOUR_GOOGLE_API_KEY replaced with your actual key.
                <script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=YOUR_GOOGLE_API_KEY&sensor=false"></script>
        -->

        <script type="text/javascript">
        var customIcons = {
          restaurant: {
            icon: 'http://labs.google.com/ridefinder/images/mm_20_blue.png'
          },
          bar: {
            icon: 'http://labs.google.com/ridefinder/images/mm_20_red.png'
          }
        };

        function downloadUrl(url,callback) {
         var request = window.ActiveXObject ?
             new ActiveXObject('Microsoft.XMLHTTP') :
             new XMLHttpRequest;

         request.onreadystatechange = function() {
           if (request.readyState == 4) {
             request.onreadystatechange = doNothing;
             callback(request, request.status);
           }
         };

         request.open('GET', url, true);
         request.send(null);
        }

        function doNothing() {}

        </script>
        <script type="text/javascript">
            // When the window has finished loading create our google map below
            google.maps.event.addDomListener(window, 'load', init);
        
            function init() {
                // Basic options for a simple Google Map
                // For more options see: https://developers.google.com/maps/documentation/javascript/reference#MapOptions
                var mapOptions = {
                    // How zoomed in you want the map to start at (always required)
                    zoom: 18,

                    // The latitude and longitude to center the map (always required)
                    //center: new google.maps.LatLng(49.249820, -123.001662), // New York
					center: new google.maps.LatLng(11.6059762,78.0076199),

                    // How you would like to style the map. 
                    // This is where you would paste any style found on Snazzy Maps.
                    styles: [{"featureType":"all","elementType":"all","stylers":[{"invert_lightness":true},{"saturation":10},{"lightness":30},{"gamma":0.5},{"hue":"#435158"}]}]
                };

                // Get the HTML DOM element that will contain your map 
                // We are using a div with id="map" seen below in the <body>
                var mapElement = document.getElementById('map');

                // Create the Google Map using our element and options defined above
                var map = new google.maps.Map(mapElement, mapOptions);

                var infoWindow = new google.maps.InfoWindow;

              downloadUrl("db_gen_xml.php", function(data) {
                  var xml = data.responseXML;
                  var markers = xml.documentElement.getElementsByTagName("marker");
                  for (var i = 0; i < markers.length; i++) {
					var timestamp = markers[i].getAttribute("id");
                    var name = markers[i].getAttribute("name");
                    var colour = markers[i].getAttribute("colour");
                    var address = markers[i].getAttribute("address");
                    var type = markers[i].getAttribute("type");
					var ip = markers[i].getAttribute("ip");
                    var point = new google.maps.LatLng(
                        parseFloat(markers[i].getAttribute("lat")),
                        parseFloat(markers[i].getAttribute("lng")));
                    var html = "<b>" + name + "</b> <br/>" + timestamp;
                    //var icon = customIcons[type] || {};
                    var icons = {};
                    icons["red"] = 'http://maps.google.com/mapfiles/ms/icons/red-dot.png';
                    icons["blue"] = 'http://maps.google.com/mapfiles/ms/icons/blue-dot.png';
                    icons["purple"] = 'http://maps.google.com/mapfiles/ms/icons/purple-dot.png';
                    icons["yellow"] = 'http://maps.google.com/mapfiles/ms/icons/yellow-dot.png';
                    icons["green"] = 'http://maps.google.com/mapfiles/ms/icons/green-dot.png';

                    var marker = new google.maps.Marker({
                      map: map,
                      position: point,
                      icon: icons[colour]
                    });

                    bindInfoWindow(marker, map, infoWindow, html);
                  }
                });
            }

        function bindInfoWindow(marker, map, infoWindow, html) {
          google.maps.event.addListener(marker, 'click', function() {
            infoWindow.setContent(html);
            infoWindow.open(map, marker);
          });
        }

        </script>
            <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <style>
     #btn-close-modal {
        width:100%;
        text-align: right;
        padding: 10px;
        cursor:pointer;
        color:#fff;
    }
    </style>
    </head>
    <body class = "container-fluid" onload="init()">

        <!--DEMO01-->
        <div id="animatedModal">
            <!--THIS IS IMPORTANT! to close the modal, the class name has to match the name given on the ID -->
            <div id="btn-close-modal" class="close-animatedModal"> 
                <img src = "img/closebt.svg">
            </div>
                
            <div class="modal-content" style = "font-size: 30px; text-align: center;">
                <!--Your modal content goes here-->
                <h1>Users</h1>
                <?php
                    $query = "SELECT * FROM users";
                    $result = mysql_query($query);
                    if (!$result) {
                      die('Invalid query: ' . mysql_error());
                    }

                    while ($row = mysql_fetch_array($result)) {
                        echo '<li>' . $row['user_name'] . '</li>';
                    }
                ?>
            </div>
			<div class="modal-content" style = "font-size: 30px; text-align: center;">
                <!--Your modal content goes here-->
                <h1>Information</h1>
                <?php
                    $query = "SELECT * FROM markers";
                    $result = mysql_query($query);
                    if (!$result) {
                      die('Invalid query: ' . mysql_error());
                    }

					echo '<table style = "text-align:center">';
                    echo '<tr>
                            <th>Timestamp</th>
                            <th>Name</th>
                            <th>Latitude</th>
                            <th>Longitude</th>
                            <th>IP Address</th>
                          </tr>';
                    while ($row = mysql_fetch_array($result)) {
						echo '<tr>';
                        echo '<td>' . $row['id'] . '</td>';
						echo '<td>' . $row['name'] . '</td>';
                        echo '<td>' . $row['lat'] . '</td>';
                        echo '<td>' . $row['lng'] . '</td>';
						echo '<td>' . $row['ip'] . '</td>';
						echo '</tr>';
                    }
					echo '</table>';
                ?>
            </div>
        </div>

        <!-- Navigation -->
        <nav class="navbar navbar-inverse navbar-fixed-bottom" role="navigation">
            <div class="container-fluid">
                <!-- Brand and toggle get grouped for better mobile display -->
                <div class="navbar-header" style = "padding-left: 15px">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="#">Android GPS Tracker | <?php echo $userRow['user_name']; ?></a>
                </div>
                <!-- Collect the nav links, forms, and other content for toggling -->
                <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                    <ul class="nav navbar-nav">
                        <li>
                            <a id="demo01" href="#animatedModal">Information</a>
                        </li>
                        <li>
                            <a href="logout.php?logout">Sign Out</a>
                        </li>
                    </ul>
                </div>
                <!-- /.navbar-collapse -->
            </div>
            <!-- /.container -->
        </nav>

        <!-- Page Content -->
        <div id = "main-content" class="container-fluid">
            <div class="row-fluid">
                <div class="col-md-12 col-sm-12" style = "padding:0">
                    <div id="map"></div>
                </div>
            </div>
            <!-- /.row -->
        </div>
        <!-- /.container -->

        <div class="md-modal md-effect-1" id="modal-1">
            <div class="md-content">
                <h3>Modal Dialog</h3>
                <div>
                    <p>This is a modal window. You can do the following things with it:</p>
                    <ul>
                        <li><strong>Read:</strong> Modal windows will probably tell you something important so don't forget to read what it says.</li>
                        <li><strong>Look:</strong> modal windows enjoy a certain kind of attention; just look at it and appreciate its presence.</li>
                        <li><strong>Close:</strong> click on the button below to close the modal.</li>
                    </ul>
                    <button class="md-close">Close me!</button>
                </div>
            </div>
        </div>
        <!-- jQuery -->
        <script src="js/jquery.js"></script>
        <script src="js/animatedModal.js"></script>

        <!-- Bootstrap Core JavaScript -->
        <script src="js/bootstrap.min.js"></script>

        <script>
          $("#demo01").animatedModal();
        </script>
    </body>
</html>
