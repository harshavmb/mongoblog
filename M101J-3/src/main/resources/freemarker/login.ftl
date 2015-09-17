<!DOCTYPE html>
<html lang="en">
<#import "lib/header.ftl" as h>
<#import "lib/footer.ftl" as f>
<#import "lib/trhnloginsignup.ftl" as trhn>
<#import "lib/corousel.ftl" as c>
<@h.page>
</@h.page>
<link rel="stylesheet" href="https://googledrive.com/host/0B89YLZTwQ7ZCS3BPNUFEeWRrdzA/">
<style type="text/css">
      .error {color: red}
</style>
<body>

    <@trhn.page>
    </@trhn.page>
    <!-- Page Header -->
    <!-- Set your background image for this header on the line below. -->
    <@c.page>
    </@c.page>
    
    <div class="loginpanel">
    <form method="post">
  <div class="txt">
    <input name="username" type="text" placeholder="Username" value="${username}"/>
    <label for="username" class="entypo-user"></label>
  </div>
  <div class="error">
  ${username_error!""}
  </div>
  <div class="txt">
    <input name="password" type="password" placeholder="Password" value=""/>
    <label for="password" class="entypo-lock"></label>
  </div>
  <div class="error">
	  ${login_error}
  </div>
  <div class="form-group col-xs-12">
    <button type="submit" class="btn btn-success">Login</button> &nbsp; &nbsp; 
    <span>
      <a href="/signup" class="entypo-user-add register">Register</a>
    </span>
  </div>
  </form>
  <div class="hr">
    <div></div>
    <div>OR</div>
    <div></div>
  </div>
  
  <div class="social">
    <a href="onclick="+"Login();"" class="facebook"></a>
    <a href="javascript:void(0)" class="twitter"></a>
    <a href="javascript:void(0)" class="googleplus"></a>
  </div>
</div>

   <span class="resp-info"></span>
    <script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>

    <hr>
<@f.page>
</@f.page>
</body>

</html>    
    