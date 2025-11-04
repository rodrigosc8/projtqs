Feature: Search Booking
  Background:
    Given a booking exists with token "ABC123"

  Scenario: User searches for existing booking
    Given a user is on the homepage
    When the user navigates to the search page
    And the user enters token "ABC123"
    When the user clicks the search button
    Then the booking details are displayed
    And the details contain "ABC123"

  Scenario: User searches for non-existent booking
    Given a user is on the homepage
    When the user navigates to the search page
    And the user enters token "INVALID"
    When the user clicks the search button
    Then an error message is shown
