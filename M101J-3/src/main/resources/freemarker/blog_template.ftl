<!DOCTYPE html>
<html lang="en">
<#import "lib/header.ftl" as h>
<#import "lib/footer.ftl" as f>
<#import "lib/toprighthandnav.ftl" as trhn>
<#import "lib/corousel.ftl" as c>
<@h.page>
</@h.page>
<body>

    <@trhn.page>
    </@trhn.page>
    <!-- Page Header -->
    <!-- Set your background image for this header on the line below. -->
    <@c.page>
    </@c.page>

<!-- Main Content -->
<div class="container">
        <div class="row">
            <div class="col-lg-8 col-lg-offset-2 col-md-10 col-md-offset-1">
               <#list myposts as post>
                 
                  <div class="post-preview">
    <h2><a href="/post/${post["permalink"]}">${post["title"]}</a></h2>
    Posted ${post["date"]?datetime} <i>By ${post["author"]}</i><br><br>
    <strong>Comments</strong>:
    <#if post["comments"]??>
        <#assign numComments = post["comments"]?size>
            <#else>
                <#assign numComments = 0>
    </#if>

    <a href="/post/${post["permalink"]}">${numComments}</a>
    <hr>
    ${post["body"]!""}
    <p>
        <em><strong>Filed Under</strong></em>:
        <#if post["tags"]??>
            <#list post["tags"] as tag>
                 ${tag}
            </#list>
        </#if>
   </div> 
</#list>
</div>
</div>
</div>

<br>
<@f.page>
</@f.page>
</body>
</html>

