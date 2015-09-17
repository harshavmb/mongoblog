<!DOCTYPE html>
<html lang="en">
<#import "lib/header.ftl" as h>
<#import "lib/footer.ftl" as f>
<#import "lib/toprighthandnav.ftl" as trhn>
<@h.page>
</@h.page>
<style type="text/css">
      .error {color: red}
</style>
<body>
    <@trhn.page>
    </@trhn.page>
    <!-- Page Header -->
    <!-- Set your background image for this header on the line below. -->
    <header class="intro-header" style="background-image: url('https://googledrive.com/host/0B89YLZTwQ7ZCS2t4V09CNDhiSUk/')">
        <div class="container">
            <div class="row">
                <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                    <div class="page-heading">
                        <h1>Contact Me</h1>
                        <hr class="small">
                        <span class="subheading">Have questions? I have answers (maybe).</span>
                    </div>
                </div>
            </div>
        </div>
    </header>
    
    <!-- Main Content -->
    <div class="container">
        <div class="row">
            <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                <p>Want to get in touch with me? Fill out the form below to send me a message and I will try to get back to you asap!</p>
                <!-- Contact Form - Enter your email address on line 19 of the mail/contact_me.php file to make this form work. -->
                <!-- WARNING: Some web hosts do not allow emails to be sent through forms to common mail hosts like Gmail or Yahoo. It's recommended that you use a private domain email address! -->
                <!-- NOTE: To use the contact form, your site must be on a live web host with PHP! The form will not work locally! -->
                <form name="sentMessage" id="contactForm" novalidate method="post">
                    <div class="row control-group">
                        <div class="form-group col-xs-12 floating-label-form-group controls">
                            <label>Name</label>
                            <input type="text" class="form-control" placeholder="Name" id="name" name="contactName" value="" required data-validation-required-message="Please enter your name.">
                            <p class="help-block text-danger"></p>
                        </div>
                    </div>
                    <div class="row control-group">
                        <div class="form-group col-xs-12 floating-label-form-group controls">
                            <label>Email Address</label>
                            <input type="email" class="form-control" placeholder="Email Address" id="email" name="contactEmail" value="" required data-validation-required-message="Please enter your email address.">
                            <p class="help-block text-danger"></p>
                        </div>
                    </div>
                   <div class="row control-group">
                        <div class="form-group col-xs-12 floating-label-form-group controls">
                            <label>Message</label>
                            <textarea rows="5" class="form-control" placeholder="Message" id="message" name="contactMessage" value="" required data-validation-required-message="Please enter a message."></textarea>
                            <p class="help-block text-danger"></p>
                        </div>
                    </div>
                    <div class="error">
	                  ${contact_error}
                    </div>
                    <br>
                    <div class="form-group col-xs-12">
                        <button type="submit" class="btn btn-success">SEND</button> &nbsp; &nbsp;
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
    