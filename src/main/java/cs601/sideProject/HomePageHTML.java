package cs601.sideProject;

public class HomePageHTML {

    public String getHomePageHTML(String user, String columnContent, String table) {
        return String.format("""
                
                <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
                       <html xmlns="http://www.w3.org/1999/xhtml">
                         <html>
                         <head>
                             <title>Inventory Management</title>
                             <meta charset="utf-8">
                             <meta name="viewport" content="width=device-width, initial-scale=1">
                             <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
                             <style>
                             body {
                               margin: 20;
                             }
                             
                             table {
                               white-space: nowrap;
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
                                margin-right: 160px;
                             }
                             
                             #newitem {
                                float: right;
                                margin-right: 40px;
                             }
                             
                             .modal {
                                display: none; /* Hidden by default */
                                position: fixed; /* Stay in place */
                                z-index: 1; /* Sit on top */
                                padding-top: 100px; /* Location of the box */
                                left: 0;
                                top: 0;
                                width: 100%%; /* Full width */
                                height: 100%%; /* Full height */
                                overflow: auto; /* Enable scroll if needed */
                                background-color: rgb(0,0,0); /* Fallback color */
                                background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
                              }
                              
                              /* Modal Content */
                              .modal-content {
                                background-color: #fefefe;
                                margin: auto;
                                padding: 20px;
                                border: 1px solid #888;
                                width: 60%%;
                              }
                              
                              /* The Close Button */
                              .close {
                                color: #aaaaaa;
                                float: right;
                                font-size: 28px;
                                font-weight: bold;
                              }
                              
                              .close:hover,
                              .close:focus {
                                color: #000;
                                text-decoration: none;
                                cursor: pointer;
                              }
                             
                             #itemname,#price, #quantity, #comment,#categoery, #brand{
                               width: 100%%;
                               padding: 12px 20px;
                               margin: 8px 0;
                               display: inline-block;
                               border: 1px solid #ccc;
                               box-sizing: border-box;
                              
                             }
                             
                             #confirm {
                               background-color: #04AA6D;
                               color: white;
                               border: none;
                               margin: 8px 0;
                               cursor: pointer;
                               padding: 14px 20px;
                             }
                             
                             .welcome {
                               font-size: 40px;
                               font-weight: bold;
                               vertical-align: middle;
                             }
                             
                             .name {
                               font-size: 20px;
                               color: #00CED1;
                               vertical-align: middle;
                               
                             }
                             
                                         
                             
                             .column {
                               float: left;
                               padding: 10px;
                               margin-left: 20px;
                             }
                             
                             .column.side {
                               width: 25%%;
                               border: 2px solid grey;
                               margin-top: 10px;
                               margin-bottom: 10px;
                               height: 400px;
                             }
                                         
                             .column.middle {
                               width: 45%%;
                               border: 2px solid grey;
                               margin-top: 10px;
                               margin-bottom: 10px;
                               height: 400px;
                               overflow: scroll;
                             }
                             
                             .row:after {
                               content: "";
                               display: table;
                               clear: both;
                             }
                                         
                             @media screen and (max-width: 600px) {
                               .column.side, .column.middle {
                                 width: 100%%;
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
                         <form action="/home" method="POST" accept-charset="utf-8">
                            <input type="text" name="search" value=""/>
                            <button id='search' type='submit'>Search</button>
                         </form>
                         
                            <button id="newitem">Add New Item</button>
                            <button onclick="location.href = 'http://localhost:1024/home';" id="myButton" class="float-left submit-button" >Home</button>
                                
                             <!-- The Modal -->
                             <div id="myModal" class="modal">
                           
                             <!-- Modal content -->
                             <div class="modal-content">
                               <span class="close">&times;</span>
                               <label id="name" for="itemname"><b>Name</b></label>
                               <input id="itemname" type="text" placeholder="Enter Item name" name="iname" required>
                        
                               <label for="brand"><b>Brand</b></label>
                               <input id="brand" type="text" placeholder="Enter Brand" name="brand" required>
                               
                                <label for="category"><b>Category</b></label>
                                <select name="category" id="categoery">
                                  <option value="select">---Please select---</option>
                                  <option value="handbag">Handbag</option>
                                  <option value="shoes">Shoes</option>
                                  <option value="cloth">Cloth</option>
                                  <option value="food">Food</option>
                                  <option value="beauty">Beauty</option>
                                  <option value="grocery">Grocery</option>
                                  <option value="health">Health Product</option>
                                  <option value="accessory">Accessory</option>
                                </select>
                                
                               <label for="price"><b>Price</b></label>
                               <input id="price" type="text" placeholder="Enter Price" name="price" required>
                         
                               <label for="quantity"><b>Qty</b></label>
                               <input id="quantity" type="text" placeholder="Enter Item Qty" name="qty" required>
                        
                               <label for="comment"><b>Comment</b></label>
                               <input id="comment" type="text" placeholder="Enter comment" name="comment" >
                        
                               <button id="confirm" type="submit" >Confirm</button>
                         
                         
                        </div>
                        </div>
                                   
                        <script>
                        
                            
                           
                           // Get the modal
                           var modal = document.getElementById("myModal");
                           
                           // Get the button that opens the modal
                           var btn = document.getElementById("newitem");
                           
                           var conf = document.getElementById("confirm");
                           conf.onclick = function(){
                              modal.style.display = "none";
                               $.ajax({
                                   type: 'POST',
                                   url: '/CreateItemHandler',
                                   data: JSON.stringify ({
                                     itemname: $("#itemname").val(),
                                     brand: $("#brand").val(),
                                     category: $("#categoery").val(),
                                     price: $("#price").val(),
                                     quantity: $("#quantity").val(),
                                     comment: $("#comment").val()
                                   }),
                                   success: function(data) {
                                     alert('data: ' + data["test"]);
                                     window.location.href = "http://localhost:1024/home";
                                     $("#itemname").val('');
                                     $("#brand").val('');
                                     $("#categoery").val('');
                                     $("#price").val('');
                                     $("#quantity").val('');
                                     $("#comment").val('');
                                   },
                                   contentType: "application/json",
                                   dataType: 'json'
                               });
                           }
                           
                           // Get the <span> element that closes the modal
                           var span = document.getElementsByClassName("close")[0];
                           
                           // When the user clicks the button, open the modal
                           btn.onclick = function() {
                             modal.style.display = "block";
                           }
                           
                           // When the user clicks on <span> (x), close the modal
                           span.onclick = function() {
                             modal.style.display = "none";
                           }
                           
                           // When the user clicks anywhere outside of the modal, close it
                           window.onclick = function(event) {
                             if (event.target == modal) {
                               modal.style.display = "none";
                             }
                           }
                           
                           function form_update() {
                             $("#formAction").val('UPDATE');
                           }
                           function form_delete() {
                             $("#formAction").val('DELETE');
                           }
                           
                        </script>
                           
                        </div>
                                  
                          <div class="row">
                            <div class="column side">
                              <p class="welcome"> *Welcome back* </p>
                              <p class="name"> %s </p> 
                               <button id="logout">Logout</button>
                              <div class="container">
                              
                              </div>    
                          </div>
                          
                          <div class="column middle">
                              <h3>%s</h3>
                              <p> %s</p>
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
                       """, user, columnContent,table);

    }
}
