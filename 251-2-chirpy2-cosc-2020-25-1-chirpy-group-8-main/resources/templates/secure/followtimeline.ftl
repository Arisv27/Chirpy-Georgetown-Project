<#include "/macros/navbar.ftl" />
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Following Timeline</title>
    <!-- PicoCSS -->
    <link
        rel="stylesheet"
        href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
</head>

<body>
    <header class="container">
        <@navbar username=username />
        <hgroup>
            <h1>Following Timeline</h1>
            <p>See what the chirpers you follow are chirping about!</p>
        </hgroup>
        <hr />
    </header>
    <main class="container">
        <#if chirps?? && (chirps?size> 0)>
            <#list chirps as chirp>
                <article>
                    <header style="display: flex; justify-content: space-between;">
                        <div>
                            <strong>
                                ${chirp.ownerUsername}
                            </strong>
                            <small>
                                <#if chirp.formattedTime??>
                                    &#x2014; ${chirp.formattedTime}
                                    <#else>
                                        N/A
                                </#if>
                            </small>
                    </header>
                    <p>
                        ${chirp.content}
                    </p>
                </article>
            </#list>
            <#else>
                <p>There's nothing here yet. <a href="/timeline/">Find people to follow!</a></p>
                <#-- Debugging information -->
                    <#--  <p>chirps??: ${chirps???string('true', 'false')}
                    </p>
                    <p>chirps?size: ${chirps?size}
                    </p>  -->
        </#if>
    </main>
</body>

</html>