[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/Llg9o6Tu)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=18640321)
# Programming Assignment: Chirpy 2.0
## COSC 2020 - Advanced Programming

## Required Functionality

For this homework assignment, you are completing Chirpy.  Specifically, your instantiation of Chirpy should support:

- All of the required functionality for Chirpy 1.0
- If you have not already done so, the "extra" functionality that you've designed for Chirpy 1.0;
- A logout functionality that cancels/closes the session of the currently logged in user;
- Posting Chirps;
- Timeline or feed, that displays Chirps;
- Search functionality (see below); and
- Functional navigation (see below)

### Search

A Chirper can either:

1. type in the name of another Chirper and view that latter Chirper's chirps; or
2. search for posts that contain a given hashtag.

For example, a Chirper named `Micah1` can either search for Chirps from another Chirper, say, `DeGioia`, or for a hashtag, say, `#Georgetown`.

### Functional Navigation

A user should be able to navigate Chirpy by clicking on links.  With the exception of typing in the initial URL to get to Chirpy, the site should be "connected" and not require the user to manually enter URLs to reach certain website functionality.

## Additional Requirements
 
### Source code management

As with Chirpy 1.0, you must use branches.  The requirements remain the same as were for Chirpy 1.0.

### Documentation

You must update DESIGN.md to be up-to-date with your design for Chirpy 2.0.

New: Your code should be appropriately commented with Javadoc. 

### Unit Testing

New:  The new functionality you add for Chirpy 2.0 should have unit tests, written in JUnit.  You must have at least 90% coverage of the new functions that you create for this assignment (meaning: at least 90% of the functions should have one or more JUnit cases).  The JUnit test cases must pass and need to actually test functionality.  (For example, you can't have meaningless test cases that test whether `true==true`.)

You should focus your unit test design/implementation on the functionality you provide in your data object and business logic layers, as they are the most unique (to your group) portions of your code. The display logic layer mostly considers HTTP request handling and unit testing is not needed (while it's certainly possible to unit test the handle() methods, such tests would need to compare output HTTP responses/HTML to a known correct output, which is at the very least a large, involved I/O comparison).

You may want to dedicate one member of your team to be the test case developer.  Ideally, the person creating the test cases for code should NOT be the person that wrote that code.

## Submitting your project

As with all assignments in this class, you'll be submitting via GitHub. To share with the instructors and the TAs, you should push your code to the `main` branch of your Chirpy 2.0 repository.

## Grading rubric

This homework assignment is worth 150 points, split evenly between the design document (75 points) and the implementation (also 75 points).

### Design document

Potential deductions include, but are not limited to:

- -15: incorrect format. The design document should be saved as a Markdown file and be called DESIGN.md.
- up to -30: core functionalities are not described. The design document should describe the mechanisms for each of the major functions of the service
- up to -15: omissions in the DAO
- -10: no mention of how cookies are used
- -15: inconsistencies with what's in the actual code

### Implementation

Potential deductions include, but are not limited to:

- up to -40: does not successfully implement required functionality
- -30: does not implement "extra" functionality (see above)
- up to -20: program instability / crashes
- up to -10: failure to adhere to class code style guidelines
- up to -15: no Javadoc commenting
- up to -25: no JUnit testing
- up to -20: insufficient JUnit testing (coverage less than 90% of new functions)