<#include "/macros/navbar.ftl" />
<#include "/macros/footer.ftl" />
<html>

<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Post a Chirp!</title>
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
      <ul>
        <li><a href="/">Home</a></li>
        <li><a href="/register/">Register</a></li>
        <li><a href="/login/">Log In</a></li>
        <li><a href="/listusers/">List Users</a></li>
        <li><a href="/showcookies/">Show Cookies</a></li>
      </ul>
    </nav>
    <hgroup>
      <h1>Post a Chirp</h1>
      <p>Make it good!</p>
    </hgroup>
    <hr />
  </header>
  <!-- Main -->
  <main class="container">
    <!-- Form -->
    <!-- Error Modal -->
    <#if login_succeeded?? && login_succeeded==false>
      <article>
        <header>
          <p>
            <strong>&#128721; There was an error logging in to your account!</strong>
          </p>
        </header>

        <body>
          <p>
            The username and password you provided does not match one in our system
          </p>
        </body>
      </article>
    </#if>
    <!--  Success Modal  -->
    <#if login_succeeded?? && login_succeeded==true>
      <article>
        <header>
          <p>
            <strong>&#9989; You are now logged in!</strong>
          </p>
        </header>
        <p>
          The rest of Chirpy is coming soon. Thank you for your patience!
        </p>
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
          aria-invalid="${username_invalid_state!}"
          aria-describedby="username-helper">
        <small id="username-helper">
          ${username_helper_message!}
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
          aria-invalid="${password_invalid_state!}"
          aria-describedby="password-helper">
        <small id="password-helper">
          ${password_helper_message!}
        </small>
      </fieldset>
      <button type="submit">Sign Up</button>
    </form>
  </main>
  <#if users??>
        <@footer users=users />
        <#else>
            <p>No registered users.</p>
    </#if>
</body>

</html>