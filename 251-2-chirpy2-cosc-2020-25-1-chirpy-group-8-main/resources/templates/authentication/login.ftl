<#include "/macros/footer.ftl" />
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Log In to Chirpy</title>
    <!-- PicoCSS -->
    <link
        rel="stylesheet"
        href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
</head>

<body>
    <!-- Header -->
    <header class="container">
        <nav>
            <ul>
                <li><strong>Chirpy</strong></li>
            </ul>
        </nav>
        <hgroup>
            <h1>Log In</h1>
            <p>Welcome back!</p>
        </hgroup>
        <hr />
    </header>
    <!-- Main -->
    <main class="container">
        <!-- Form -->
        <!-- Error Modal -->
        <#if errorModalMessage??>
            <article>
                <header>
                    <p>
                        <strong>&#128721; There was an error logging in to your account!</strong>
                    </p>
                </header>

                <body>
                    <p>
                        ${errorModalMessage}
                    </p>
                </body>
            </article>
        </#if>
        <!-- Form -->
        <form
            method="post"
            action="/login/">
            <!-- Username -->
            <fieldset>
                <label for="username">Username</label>
                <input
                    type="text"
                    id="username"
                    name="username"
                    value="${username!}"
                    aria-invalid="${usernameAriaInvalid!}"
                    aria-describedby="username-helper">
                <small id="username-helper">
                    ${usernameHelperMessage!}
                </small>
            </fieldset>
            <!-- Password -->
            <fieldset>
                <label for="password">Password</label>
                <input
                    type="password"
                    id="password"
                    name="password"
                    value="${password!}"
                    aria-invalid="${passwordAriaInvalid!}"
                    aria-describedby="password-helper">
                <small id="password-helper">
                    ${passwordHelperMessage!}
                </small>
            </fieldset>
            <button type="submit">Log In</button>
        </form>
        <p style="text-align: center;">Don't have an account? <a href="/register/">Sign Up</a></p>
    </main>
    <#if users??>
        <@footer users=users />
        <#else>
            <p>No registered users.</p>
    </#if>
</body>

</html>