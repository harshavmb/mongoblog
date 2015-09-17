<#macro page>

<!--For logout functionality -->
    <script type="text/javascript">
       history.forward();
    </script>
    
    
<!-- Navigation -->
    <nav class="navbar navbar-default navbar-custom navbar-fixed-top">
        <div class="container-fluid">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header page-scroll">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="/">Harsha's Blog</a>
            </div>

            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav navbar-right">
                    <#if username??>
                       <li>
                        <a href="#">${username}</a>
                       </li>
                    <#else>
                       <li>
                        <a href="/login">Login</a>
                       </li>
                    </#if>
                    <li>
                        <a href="/about">About</a>
                    </li>
                    <li>
                        <a href="/">Posts</a>
                    </li>
                    <li>
                        <a href="/contact">Contact</a>
                    </li>
                    <#if username??>
                       <li>
                         <a href="/logout">Log Out</a>
                       </li>
                     <#else>
                    </#if>
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </div>
        <!-- /.container -->
    </nav>

</#macro>