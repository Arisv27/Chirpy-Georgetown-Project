<#include "/macros/navbar.ftl" />
<#include "/macros/footer.ftl" />
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Discover Timeline</title>
    <!-- PicoCSS -->
    <link
        rel="stylesheet"
        href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
</head>

<body>
    <header class="container">
        <@navbar username=username />
        <hgroup>
            <h1>Discover Timeline</h1>
            <p>See what others are chirping about!</p>
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
                        </div>
                        <#if chirp.ownerUsername != username>
                            <form
                                method="post"
                                action="/timeline/"
                                style="margin: 0;">
                                <#if followedUsers?seq_contains(chirp.ownerUsername)>
                                    <input type="hidden" name="targetUser" value="${chirp.ownerUsername}">
                                    <input type="hidden" name="action" value="unfollow">
                                    <button type="submit" style="max-width: 128px; padding: 4px 16px; margin: 0;" class="secondary">Unfollow</button>
                                    <#else>
                                        <input type="hidden" name="targetUser" value="${chirp.ownerUsername}">
                                        <input type="hidden" name="action" value="follow">
                                        <button type="submit" style="max-width: 128px; padding: 4px 16px; margin: 0;">Follow</button>
                                </#if>
                            </form>
                        </#if>
                    </header>
                    <p>
                        ${chirp.content}
                    </p>
                </article>
            </#list>
            <#else>
                <p>There's nothing here! <a href="/postchirp/">Start the conversation</a></p>
                <#-- Debugging information -->
                    <#-- <p>chirps??: ${chirps???string('true', 'false')}
                        </p>
                        <p>chirps?size: ${chirps?size}
                        </p> -->
        </#if>
    </main>
    <#if users??>
        <@footer users=users />
        <#else>
            <p>No registered users.</p>
    </#if>
</body>

</html>