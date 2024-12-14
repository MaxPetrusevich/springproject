async function sendApiRequest(url, method = 'POST', data = null) {
    try {
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
        };
        
        if (data) {
            options.body = JSON.stringify(data);
        }
        
        const response = await fetch(url, options);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.includes("application/json")) {
            return await response.json();
        }
        
        return true;
    } catch (error) {
        console.error('Error:', error);
        throw error;
    }
}

function showNotification(message, type = 'success') {
    alert(message);
}

function confirmAction(message) {
    return confirm(message);
}

async function handleTableAction(event, url, method, confirmMessage) {
    event.preventDefault();
    
    if (confirmMessage && !confirmAction(confirmMessage)) {
        return;
    }
    
    try {
        await sendApiRequest(url, method);
        window.location.reload();
    } catch (error) {
        showNotification('Произошла ошибка при выполнении действия', 'error');
    }
} 