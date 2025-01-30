// bidProcessing.js
function scheduleBidProcessing() {
    console.log("Button Pressed");

    const frequency = document.getElementById("frequency").value;
    const dayOfWeek = document.getElementById("dayOfWeek").value;

    // Log the types and values of frequency and dayOfWeek
    console.log("Type of frequency:", typeof frequency, "Value:", frequency);
    console.log("Type of dayOfWeek:", typeof dayOfWeek, "Value:", dayOfWeek);

    if (frequency && dayOfWeek) {
        // Log values before sending
        console.log("Sending data to Firebase:", { frequency, dayOfWeek });

        // Send POST request to Firebase Function
        fetch('https://us-central1-fyp-bidder.cloudfunctions.net/updateSchedule', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                frequency: frequency,
                dayOfWeek: dayOfWeek
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Success:', data);

            // Show success message
            displayMessage("Schedule successfully set!", "success");
        })
        .catch(error => {
            console.error('There has been a problem with your fetch operation:', error);

            // Show error message
            displayMessage("Error: Could not set schedule.", "error");
        });
    } else {
        alert("Please select both frequency and day.");
    }
}

function displayMessage(message, type) {
    const messageContainer = document.getElementById("message-container");

    // Set message text and style based on success or error
    messageContainer.textContent = message;
    if (type === "success") {
        messageContainer.style.color = "green";
    } else if (type === "error") {
        messageContainer.style.color = "red";
    }

    // Make the message visible
    messageContainer.style.display = "block";

    // Hide message after 5 seconds
    setTimeout(() => {
        messageContainer.style.display = "none";
    }, 5000);
}
