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
    <header class="intro-header" style="background-image: url('https://googledrive.com/host/0B89YLZTwQ7ZCRE9PS3kwcFFINlk/')">
        <div class="container">
            <div class="row">
                <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                    <div class="post-heading">
                        <h1>${post["title"]}</h1>
                        <h2 class="subheading">Problems look mighty small from 150 miles up</h2>
                        <span class="meta">Posted by ${post["author"]} on ${post["date"]?datetime}</span>
                    </div>
                </div>
            </div>
        </div>
    </header>
     <!-- Main Content -->
    <div class="container">
        <div class="row">
            <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
                <div class="post-preview">
                        <!--<h2 class="post-title">
                            ${post["title"]}
                        </h2>
                    <p class="post-meta">Posted ${post["date"]?datetime}<i> By ${post["author"]}</i><br></p>
                    <hr>-->
                    <p>
                      ${post["body"]}
                    </p>
                    <hr>
                    <p>
                       <em><h3>Filed Under</h3></em>:
                       <#if post["tags"]??>
                        <#list post["tags"] as tag>
                          ${tag}
                        </#list>
                       </#if>
                    <p>
                      <h3>Comments:</h3>
<ul>
    <#if post["comments"]??>
        <#assign numComments = post["comments"]?size>
            <#else>
                <#assign numComments = 0>
    </#if>
    <#if (numComments > 0)>
        <#list 0 .. (numComments -1) as i>

                <strong>Author</strong>: ${post["comments"][i]["author"]}<br>
            <br>
            ${post["comments"][i]["body"]}<br>
            <hr>
        </#list>
    </#if>
    <h3>Add a comment</h3>

    <form action="/newcomment" method="POST">
        <input type="hidden" name="permalink", value="${post["permalink"]}">
        ${errors!""}<br>
        <b>Name</b> (required)<br>
        <input type="text" name="commentName" size="60" value="${comment["name"]}"><br>
        <b>Email</b> (optional)<br>
        <input type="text" name="commentEmail" size="60" value="${comment["email"]}"><br>
        <b>Comment</b><br>
        <textarea name="commentBody" cols="60" rows="10">${comment["body"]}</textarea><br><br>
        <div class="form-group col-xs-12">
           <button type="submit" class="btn btn-success">Submit</button> &nbsp; &nbsp; 
        </div>
    </form>
</ul> 
                </div>
                <hr>
                
                <!-- Pager -->
                <ul class="pager">
                    <li class="next">
                        <a href="#">Older Posts &rarr;</a>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <hr>

<@f.page>
</@f.page>

</body>

</html>


