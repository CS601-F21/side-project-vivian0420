package cs601.sideProject;

public class LoginPageHTML {
    public String getLoginPageHTML() {
        return """
                <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
                       <html xmlns="http://www.w3.org/1999/xhtml">
                         <html>
                         <head>
                              <title>Add New Item</title>
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
                                  h1{
                                    text-align: center;
                                    margin-top: 20px;
                                  }
                                  
                                  .modal-header, h4, .close {
                                      background-color: #5cb85c;
                                      color:white !important;
                                      text-align: center;
                                      font-size: 30px;
                                    }
                                    
                                  .modal-content {
                                      margin-top:30px;
                                      width: 360px;
                                      height: 320px;
                                      float: none;
                                      margin-left: auto;
                                      margin-right: auto;
                                      border: 1px solid #888;
                                  } 
                                  
                                  #login {
                                    background-color: #5cb85c; 
                                    color: white; 
                                    padding: 14px 20px; 
                                    margin: 8px 0; 
                                    border: none; 
                                    cursor: pointer; 
                                    width: 30%; 
                                  }
                                    
                                  #username,#password{
                                     width: 100%;
                                     padding: 12px 20px;
                                     margin: 8px 0;
                                     display: inline-block;
                                     border: 1px solid #ccc;
                                     box-sizing: border-box;
                                  }      
                              </style>
                         </head>  
                         <body>
                            <div class="header">
                                    <img src="https://user-images.githubusercontent.com/86545567/138036293-5b8071bc-2f94-4785-bece-d65b244450c8.JPG" alt="Flowers in Chania" width="200" height="200">
                                    <h1 style="font-size:80px;">Inventory Management</h1>
                                    
                                  </div>
                                  
                            
                            <div class="modal-content">
                             <div class="modal-header" style="padding:10px 25px;">
                                <div><span class="glyphicon glyphicon-lock"></span> Login</div>
                             </div>
                             <div class="modal-body" style="padding:40px 50px;">
                               <form action="/login" method="post" accept-charset="utf-8">
                                 <div class="form-group">
                                   <label for="usrname"><span class="glyphicon glyphicon-user"></span><b>Username<b></label>
                                   <input id="username" type="text" class="form-control" id="usrname" name="username" placeholder="Enter email"/>
                                 </div>
                                 <div class="form-group">
                                   <label for="psw"><span class="glyphicon glyphicon-eye-open"></span> Password</label>
                                   <input id="password" type="password" class="form-control" id="psw" name="password" placeholder="Enter password"/>
                                 </div>
                                   <button id="login" type="submit" class="btn btn-success btn-block"><span class="glyphicon glyphicon-off"></span> Login</button>
                               </form>
                             </div>
                         </body>
                         </html>
                       </html>              
                """;
    }
}
