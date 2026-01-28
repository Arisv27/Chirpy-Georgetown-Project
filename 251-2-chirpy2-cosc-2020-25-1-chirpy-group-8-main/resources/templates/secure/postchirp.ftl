<#include "/macros/navbar.ftl" />
<#include "/macros/footer.ftl" />
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Post a Chirp</title>
    <!-- PicoCSS -->
    <link
        rel="stylesheet"
        href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
</head>

<body>
    <header class="container">
        <@navbar username=username />
        <hgroup>
            <h1>Post a Chirp</h1>
            <p>Share your thoughts with the world!</p>
        </hgroup>
        <hr />
    </header>
    <main class="container">
        <#if error??>
            <article>
                <header>
                    <p>
                        <strong>&#128721; There was an error posting your chirp!</strong>
                    </p>
                </header>

                <body>
                    <p>
                        ${error}
                    </p>
                </body>
            </article>
        </#if>
        <form
            method="post"
            action="/postchirp/">
            <fieldset>
                <label for="content">Chirp Content</label>
                <textarea
                    id="content"
                    name="content"
                    rows="4"
                    aria-describedby="content-helper"></textarea>
                <small id="content-helper">
                    Share your thoughts in 280 characters or less.
                </small>
            </fieldset>
            <button type="submit">Post Chirp</button>
        </form>
    </main>
    <#if users??>
        <@footer users=users />
        <#else>
            <p>No registered users.</p>
    </#if>
</body>

</html>