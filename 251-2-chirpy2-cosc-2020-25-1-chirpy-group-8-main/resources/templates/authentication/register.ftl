<#include "/macros/footer.ftl" />
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Sign Up for Chirpy</title>
    <#-- PicoCSS -->
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
            <h1>Create Account</h1>
            <p>We're so excited to have you join us!</p>
        </hgroup>
        <hr />
    </header>
    <main class="container">
        <#-- Error Modal -->
            <#if showErrorModal??>
                <article>
                    <header>
                        <p>
                            <strong>&#128721; There was an error registering your account!</strong>
                        </p>
                    </header>

                    <body>
                        <p>
                            Please check the fields below and try again.
                        </p>
                    </body>
                </article>
            </#if>
            <#-- Form -->
                <form
                    method="post"
                    action="/register/">
                    <#-- Username -->
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
                        <#-- Password -->
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
                            <#-- Confirm Password -->
                                <fieldset>
                                    <label for="confirm-password">Confirm Password</label>
                                    <input
                                        type="password"
                                        id="confirm-password"
                                        name="confirmPassword"
                                        value="${confirmPassword!}"
                                        aria-invalid="${confirmPasswordAriaInvalid!}"
                                        aria-describedby="confirm-password-helper">
                                    <small id="confirm-password-helper">
                                        ${confirmPasswordHelperMessage!}
                                    </small>
                                </fieldset>
                                <button type="submit">Sign Up</button>
                </form>
                <p style="text-align: center;">Already have an account? <a href="/login/">Log In</a></p>
    </main>
    <#if users??>
        <@footer users=users />
        <#else>
            <p>No registered users.</p>
    </#if>
</body>

</html>