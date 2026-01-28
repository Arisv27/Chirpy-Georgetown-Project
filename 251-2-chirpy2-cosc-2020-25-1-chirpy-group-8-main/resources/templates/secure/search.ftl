<#include "/macros/navbar.ftl" />
<#include "/macros/footer.ftl" />
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Search</title>
    <!-- PicoCSS -->
    <link
        rel="stylesheet"
        href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
</head>
<!-- Code below made with help from chatgpt -->
<!-- https://chatgpt.com/share/67e711cb-25cc-8007-8e12-efba07767899 -->

<body>
    <header class="container">
        <@navbar username=username />
        <hgroup>
            <h1>Search</h1>
            <p>Find chirps that match your interests!</p>
            <p>Enter a Chirper's username (@username) or a tag (#tag)</p>
        </hgroup>
        <hr />
    </header>
    <main class="container">
        <!-- Form -->
        <!-- Error Modal -->
        <#if errorModalMessage??>
            <article>
                <header>
                    <p>
                        <strong>&#128721; There was an error making your query!</strong>
                    </p>
                </header>

                <body>
                    <p>
                        ${errorModalMessage}
                    </p>
                </body>
            </article>
        </#if>
        <form method="post" action="/search/">
            <fieldset>
                <label for="query">Search Chirps</label>
                <input
                    type="text"
                    id="query"
                    name="query"
                    value="${query!}"
                    placeholder="Enter your query here."
                    required>
                <button type="submit">Search</button>
            </fieldset>
        </form>
        <hr />
        <#if results?? && (results?size> 0)>
            <#list results as chirp>
                <article>
                    <header>
                        <p>
                            <strong>
                                ${chirp.ownerUsername}
                            </strong>
                            <small>
                                <#if chirp.timestamp??>
                                    ${chirp.timestamp}
                                    <#else>
                                        N/A
                                </#if>
                            </small>
                        </p>
                    </header>
                    <p>
                        ${chirp.content}
                    </p>
                </article>
            </#list>
            <#else>
                <p>No results found for your search.</p>
        </#if>
    </main>
    <#if users??>
        <@footer users=users />
        <#else>
            <p>No registered users.</p>
    </#if>
</body>

</html>