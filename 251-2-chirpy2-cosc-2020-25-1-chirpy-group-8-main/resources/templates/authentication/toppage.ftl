<#include "/macros/footer.ftl" />
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Welcome to Chirpy!</title>
    <!-- PicoCSS -->
    <link
        rel="stylesheet"
        href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
</head>

<body>
    <header class="container">
        <nav>
            <ul>
                <li><strong>Chirpy</strong></li>
            </ul>
        </nav>
        <hgroup>
            <h1>Welcome to Chirpy!</h1>
            <p>Where conversations happen</p>
        </hgroup>
        <hr />
    </header>
    <main class="container">
        <p>
            Jump in and be part of the conversation! Whether you're sharing your thoughts, keeping up with trends, or just seeing what's new, Chirpy makes it easy to connect and engage.
        </p>
        <ul style="padding-bottom: 32px;">
            <li>&#128640; <strong>Post</strong> your thoughts in an instant</li>
            <li>&#128101; <strong>Follow</strong> friends and creators you love</li>
            <li>&#10084;&#65039; <strong>Like</strong> and engage with the community</li>
        </ul>
        <div style="display: flex; gap: 16px;">
            <a
                href="/register/">
                <button>Create Account</button>
            </a>
            <a
                href="/login/">
                <button class="outline secondary">Log In</button>
            </a>
        </div>
    </main>
    <#if users??>
        <@footer users=users />
        <#else>
            <p>No registered users.</p>
    </#if>
</body>

</html>