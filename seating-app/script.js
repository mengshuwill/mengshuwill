document.getElementById('createTables').addEventListener('click', () => {
    const count = parseInt(document.getElementById('tableCount').value, 10);
    const container = document.getElementById('tables');
    container.innerHTML = '';
    for (let i = 1; i <= count; i++) {
        const table = document.createElement('div');
        table.className = 'table';
        table.dataset.tableId = i;
        // create seats
        for (let j = 0; j < 10; j++) {
            const seat = document.createElement('div');
            seat.className = 'seat';
            seat.dataset.seatId = j;
            seat.addEventListener('dragover', e => e.preventDefault());
            seat.addEventListener('drop', handleDrop);
            table.appendChild(seat);
        }
        positionSeats(table);
        container.appendChild(table);
    }
});

function positionSeats(table) {
    const radius = 80;
    const seats = table.querySelectorAll('.seat');
    seats.forEach((seat, index) => {
        const angle = (index / seats.length) * Math.PI * 2;
        const x = Math.cos(angle) * radius + 90;
        const y = Math.sin(angle) * radius + 90;
        seat.style.left = `${x}px`;
        seat.style.top = `${y}px`;
    });
}

document.getElementById('addGuest').addEventListener('click', () => {
    const nameInput = document.getElementById('guestName');
    const name = nameInput.value.trim();
    if (name) {
        const guest = document.createElement('div');
        guest.className = 'guest';
        guest.textContent = name;
        guest.draggable = true;
        guest.addEventListener('dragstart', handleDragStart);
        document.getElementById('guestList').appendChild(guest);
        nameInput.value = '';
    }
});

function handleDragStart(e) {
    e.dataTransfer.setData('text/plain', e.target.textContent);
    e.dataTransfer.effectAllowed = 'move';
}

function handleDrop(e) {
    e.preventDefault();
    const name = e.dataTransfer.getData('text/plain');
    if (name) {
        e.target.textContent = name;
    }
}
