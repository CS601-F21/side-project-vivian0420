package cs601.sideProject;

public class HomePage1 {
    public String getHomePageHTML() {
        return """
                <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
                       <html xmlns="http://www.w3.org/1999/xhtml">
                         <html>
                         <head>
                         <title>Inventory Management</title>
                         <meta charset="utf-8">
                         <meta name="viewport" content="width=device-width, initial-scale=1">
                         <style>
                         body {
                           margin: 20;
                         }
                         
                         * {
                           box-sizing: border-box;
                         }
                         
                         img{
                              border-radius: 100px;
                              float: left;
                              margin-right: 100px;
                              margin-left: 50px;
                         
                            }
                         .header {
                           background-color: #f1f1f1;
                           padding: 20px;
                           text-align: center;
                           font-family: Comic Sans MS;
                         }
                         
                         .topnav {
                           overflow: hidden;
                           background-color: #666;
                           padding: 8px 16px;
                         
                         }
                         form{
                            float: right;
                            margin-right: 120px;
                          
                         }
                         
                         #add {
                            float: right;
                            margin-right: 40px;
                         }
                         .cancelbtn {
                           width: auto;
                           padding: 10px 18px;
                           background-color: #f44336;
                         }
                         
                          .container {
                            padding: 8px;
                            
                          }
                          
                          span.psw {
                            float: center;
                            padding-top: 8px;
                          }
                          
                          /* The Modal (background) */
                         .modal {
                           display: none; /* Hidden by default */
                           position: fixed; /* Stay in place */
                           z-index: 1; /* Sit on top */
                           left: 50%;
                           top: 50%;
                           width: 100%; /* Full width */
                           height: 100%; /* Full height */
                           overflow: auto; /* Enable scroll if needed */
                           background-color: rgb(0,0,0); /* Fallback color */
                           background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
                           padding-top: 100px;
                           transform: translate(-50%, -50%);
                         }
                          
                          /* Modal Content/Box */
                          .modal-content {
                            margin: auto;
                            background-color: #fefefe;
                            
                            border: 1px solid #888;
                            width: 100%; /* Could be more or less, depending on screen size */
                          }
                          
                          /* The Close Button (x) */
                          .close {
                            float: right;
                            right: 20px;
                            top: 0;
                            color: #aaaaaa;
                            font-size: 35px;
                            font-weight: bold;
                          }
                          
                          .close:hover,
                          .close:focus {
                            color: #000;
                            cursor: pointer;
                          }
                          
                           /* Add Zoom Animation */
                           .animate {
                             -webkit-animation: animatezoom 0.6s;
                             animation: animatezoom 0.6s
                           }
                           
                           @-webkit-keyframes animatezoom {
                             from {-webkit-transform: scale(0)}
                             to {-webkit-transform: scale(1)}
                           }
                           
                            @keyframes animatezoom {
                              from {transform: scale(0)}
                              to {transform: scale(1)}
                            }
                            
                            /* Change styles for span and cancel button on extra small screens */
                            @media screen and (max-width: 200px) {
                              span.psw {
                                 display: block;
                                 float: none;
                              }
                              .cancelbtn {
                                 width: 100%;
                              }
                            }
                            
                            #username,#password, #itemname,#price, #quantity, #comment{
                              width: 100%;
                              padding: 12px 20px;
                              margin: 8px 0;
                              display: inline-block;
                              border: 1px solid #ccc;
                              box-sizing: border-box;
                            }
                            
                            #confirm {
                              background-color: #04AA6D;
                              color: white;
                              padding: 14px 20px;
                              margin: 8px 0;
                              border: none;
                              cursor: pointer;
                              width: 50%;
                              margin-left: 20px;
                            }
                            
                            #login {
                              background-color: #04AA6D;
                              color: white;
                              padding: 14px 20px;
                              margin: 8px 0;
                              border: none;
                              cursor: pointer;
                              width: 100%;
                            }
                            
                            .column {
                              float: left;
                              padding: 10px;
                              margin-left: 20px;
                            }
                            
                            .column.side {
                              width: 25%;
                              border: 2px solid grey;
                              margin-top: 10px;
                              margin-bottom: 10px;
                              height: 400px;
                            }
                            
                            .column.middle {
                              width: 45%;
                              border: 2px solid grey;
                              margin-top: 10px;
                              margin-bottom: 10px;
                               height: 400px;
                             }
                             
                             .row:after {
                               content: "";
                               display: table;
                               clear: both;
                             }
                             
                             @media screen and (max-width: 600px) {
                              .column.side, .column.middle {
                                width: 100%;
                              }
                            }
                            
                            .footer {
                              background-color: #f1f1f1;
                              padding: 10px;
                              text-align: center;
                            }
                            </style>
                            </head>
                            
                            <body>
                            
                            
                             <div class="header">
                               <img src="https://user-images.githubusercontent.com/86545567/138036293-5b8071bc-2f94-4785-bece-d65b244450c8.JPG" alt="Flowers in Chania" width="200" height="200">
                               <h1 style="font-size:80px;">Inventory Management</h1>
                               
                             </div>
                             
                             <div class="topnav">
                              <form>
                                 <input type="text" name="search" value=""/>
                                 <input type="submit" value="Search" />
                               </form>
                               <button id = "add" onclick="document.getElementById('id01').style.display='block'" style="width:auto;">Add New Item</button>
                                      
                                      <div id="id01" class="modal">
                                      
                                        <form class="modal-content animate" action="/action_page.php" method="post">
                                          <div class="imgcontainer">
                                            <span onclick="document.getElementById('id01').style.display='none'" class="close" title="Close Modal">&times;</span>
                                            
                                          </div>
                                          
                                      <div class="container">
                                        <label for="itemname"><b>Name</b></label>
                                        <input id="itemname" type="text" placeholder="Enter Item name" name="iname" required>
                                  
                                        <label for="price"><b>Price</b></label>
                                        <input id="price" type="text" placeholder="Enter Price" name="price" required>
                                        
                                        <label for="quantity"><b>Qty</b></label>
                                        <input id="quantity" type="text" placeholder="Enter Item Qty" name="qty" required>
                                  
                                        <label for="comment"><b>Comment</b></label>
                                        <input id="comment" type="text" placeholder="Enter comment" name="comment" required>
                                   
                                        <button id="confirm" type="submit">Confirm</button>
                                        
                                      </div>
                                  
                                      <div class="container" style="background-color:#f1f1f1">
                                        <button id="cancel" type="button" onclick="document.getElementById('id01').style.display='none'" class="cancelbtn">Cancel</button>
                                        
                                      </div>
                                    </form>
                                  </div>
                                  
                              <script>
                                // Get the modal
                                var modal = document.getElementById('id01');
                                
                                // When the user clicks anywhere outside of the modal, close it
                                window.onclick = function(event) {
                                    if (event.target == modal) {
                                        modal.style.display = "none";
                                    }
                                }
                                </script>
                                
                              </div>
                                  
                                  <div class="row">
                                    <div class="column side">
                                      <h2>Login</h2>
                                      <div class="container">
                                      <label for="uname"><b>Username</b></label>
                                      <input id="username" type="text" placeholder="Enter Username" name="uname" required>
                                      
                                      <label for="psw"><b>Password</b></label>
                                      <input id="password" type="password" placeholder="Enter Password" name="psw" required>
                                      <button id="login" type="submit">Login</button>
                                      <label>
                                          <input type="checkbox" checked="checked" name="remember"> Remember me
                                      </label>
                                      </div>    
                                  </div>
                                  
                                  <div class="column middle">
                                      <h2>Current Inventory</h2>
                                      <p> here will be a form</p>
                                  </div>
                                  
                                  <div class="column side">
                                      <h2>Reminder</h2>
                                      <p>here will present reminder</p>
                                    </div>
                                  </div>
                                  <div class="footer">
                                    <p>Create by Vivian Zhang</p>
                                  </div>
                                </body>
                                </html>
                       </html>
                       """;

    }
}

