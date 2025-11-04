Feature: Citizen Booking Management
  As a citizen
  I want to create waste collection bookings
  So that I can schedule collection services

  Scenario: Citizen creates a booking successfully
    Given the citizen is on the homepage
    When the citizen navigates to the citizen page
    And the citizen selects municipality "Aveiro"
    And the citizen enters date "2025-11-20"
    And the citizen selects timeslot "Manhã"
    And the citizen enters description "Colchão e mobília velha"
    When the citizen submits the booking form
    Then the booking is created successfully
