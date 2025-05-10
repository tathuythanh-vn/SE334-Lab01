const seatComponent = document.querySelectorAll('.seat-component');
const message = document.querySelector('#message');
const cancelBtn = document.querySelector('#cancel-btn');
const bookingForm = document.querySelector('#bookingForm');
const selectedSeatInput = document.querySelector('#selectedSeat');

seatComponent.forEach(seat => {
    seat.addEventListener('click', () => {
        // Remove selected class from all seats first
        seatComponent.forEach(s => s.classList.remove('selected'));
        
        // Add selected class to clicked seat
        seat.classList.add('selected');
        
        // Update message and hidden input
        message.textContent = "Seat chosen: " + seat.textContent;
        selectedSeatInput.value = seat.textContent;
    });
});

cancelBtn.addEventListener('click', () => {
    const confirmed = confirm("Are you sure you want to cancel the selected seat?");
    if (confirmed) {
        message.textContent = "Seat chosen: ";
        selectedSeatInput.value = "";
        seatComponent.forEach(seat => {
            seat.classList.remove('selected');
        });
    }
});

bookingForm.addEventListener('submit', (e) => {
    if (!selectedSeatInput.value) {
        e.preventDefault();
        alert('Please select a seat first!');
    }
});
