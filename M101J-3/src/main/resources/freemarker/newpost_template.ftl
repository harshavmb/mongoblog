<!doctype HTML>
<html lang="en">
<!--<#if username??>
    Welcome ${username} <a href="/logout">Logout</a>
</#if>-->
<#import "lib/header.ftl" as h>
<#import "lib/footer.ftl" as f>
<#import "lib/toprighthandnav.ftl" as trhn>
<#import "lib/corousel.ftl" as c>
<@h.page>
</@h.page>
<link rel="stylesheet" href="css/style.css">
<style type="text/css">
      .errors{color: red}
</style>
<body>
    <@trhn.page>
    </@trhn.page>
    <!-- Page Header -->
    <!-- Set your background image for this header on the line below. -->
    <@c.page>
    </@c.page>
   <!--<form action="/newpost" method="POST">
    ${errors!""}
    <h2>Title</h2>
    <input type="text" name="subject" size="120" value="${subject!""}"><br>

    <h2>Blog Entry
        <h2>
            <textarea name="body" cols="120" rows="20">${body!""}</textarea><br>

            <h2>Tags</h2>
            Comma separated, please<br>
            <input type="text" name="tags" size="120" value="${tags!""}"><br>

            <p>
                <input type="submit" value="Submit">
            </p>
      -->      
     <!-- Main Content -->
    <div class="container">
        <div class="row">
            <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                <p>Submit the post with html tags. Will take care of parsing and presenting!</p>
                
                <!-- Contact Form - Enter your email address on line 19 of the mail/contact_me.php file to make this form work. -->
                <!-- WARNING: Some web hosts do not allow emails to be sent through forms to common mail hosts like Gmail or Yahoo. It's recommended that you use a private domain email address! -->
                <!-- NOTE: To use the contact form, your site must be on a live web host with PHP! The form will not work locally! -->
                
                <form action="/newpost" name="sentMessage" id="contactForm" novalidate method="post">
                ${errors!""}
                    <div class="row control-group">
                        <div class="form-group col-xs-12 floating-label-form-group controls">
                            <label>Title</label>
                            <input type="text" class="form-control" placeholder="Title" id="name" name="subject" value="${subject!""}" required data-validation-required-message="Please enter the title for the blog.">
                            <p class="help-block text-danger"></p>
                        </div>
                    </div>
                   <div class="row control-group">
                        <div class="form-group col-xs-12 floating-label-form-group controls">
                            <label>Blog Entry</label>
                            <textarea rows="5" class="form-control" placeholder="Blog Entry" id="message" name="body" value="${body!""}" required data-validation-required-message="Please enter the blog content."></textarea>
                            <p class="help-block text-danger"></p>
                        </div>
                    </div>
                    <div class="row control-group">
                        <div class="form-group col-xs-12 floating-label-form-group controls">
                            <label>Tags(Comma(,) seperated please!)</label>
                            <input type="text" class="form-control" placeholder="Tags(Comma(,) seperated please!)" id="tags" name="tags" value="${tags!""}" required data-validation-required-message="Please enter the tags for searching.">
                            <p class="help-block text-danger"></p>
                        </div>
                    </div>
                    
                    <br>
                    <div class="form-group col-xs-12">
                        <button type="submit" class="btn btn-success">SUBMIT</button> &nbsp; &nbsp;
                    </div>
                </form>
            </div>
        </div>
    </div>

    <hr>
    
<@f.page>
</@f.page>
</body>
</html>

