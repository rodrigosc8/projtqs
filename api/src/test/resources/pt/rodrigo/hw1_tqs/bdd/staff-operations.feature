Feature: Staff Operations
  As a staff member
  I want to filter and manage bookings
  So that I can process requests efficiently

  Background:
    Given test bookings exist for filtering

  Scenario: Staff filters bookings by municipality
    Given a staff member is on the homepage
    When the staff navigates to the staff page
    And the staff selects municipality filter "Aveiro"
    And the staff clicks the filter button
    Then only bookings from "Aveiro" are displayed

  Scenario: Staff filters bookings by status
    Given a staff member is on the homepage
    When the staff navigates to the staff page
    And the staff selects status filter "RECEIVED"
    And the staff clicks the filter button
    Then only bookings with status "RECEIVED" are displayed
