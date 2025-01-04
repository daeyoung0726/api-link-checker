const apiBase = '/v1/api/link/checker';
const swaggerBase = '/swagger-ui/index.html#/';

const apiGroupsDiv = document.getElementById('api-groups');
const messageDiv = document.getElementById('message');
const filterDropdown = document.getElementById('group-filter');
const nicknameModal = document.getElementById('nickname-modal');
const nicknameInput = document.getElementById('nickname-input');
const setNicknameButton = document.getElementById('set-nickname');
const resetNicknameButton = document.getElementById('reset-nickname');

let allGroups = {};
let swaggerLinks = {};

function setNicknameAndCloseModal() {
    nickname = nicknameInput.value.trim();
    if (nickname) {
        localStorage.setItem('nickname', nickname);
        nicknameModal.classList.remove('active');
    } else {
        showMessage('Nickname cannot be empty!', 'error');
    }
}

function getNickname() {
    return localStorage.getItem('nickname');
}

setNicknameButton.addEventListener('click', setNicknameAndCloseModal);

document.addEventListener('DOMContentLoaded', () => {
    const savedNickname = getNickname();
    if (savedNickname) {
        nicknameModal.classList.remove('active');
    } else {
        nicknameModal.classList.add('active');
    }
});

function resetNickname() {
    localStorage.removeItem('nickname');
    nicknameModal.classList.add('active');
    showMessage('Nickname has been reset. Please enter a new nickname.', 'success');
}

resetNicknameButton.addEventListener('click', resetNickname);

async function fetchApiGroups() {
    try {
        const response = await fetch(`${apiBase}`);
        if (!response.ok) throw new Error('Failed to fetch API data');
        const data = await response.json();
        allGroups = data;

        await fetchSwaggerLinks();

        populateFilterDropdown(Object.keys(data));
        renderApiGroups(data);
    } catch (error) {
        console.error('Error fetching API groups:', error);
        showMessage('Error fetching API groups', 'error');
    }
}

async function fetchSwaggerLinks() {
    try {
        const response = await fetch(`${apiBase}/swagger-links`);
        if (!response.ok) throw new Error('Failed to fetch Swagger links');
        swaggerLinks = await response.json();
    } catch (error) {
        console.error('Error fetching Swagger links:', error);
        showMessage('Error fetching Swagger links', 'error');
    }
}

function populateFilterDropdown(groupNames) {
    filterDropdown.innerHTML = '<option value="">All Groups</option>';
    groupNames.forEach(groupName => {
        const option = document.createElement('option');
        option.value = groupName;
        option.textContent = groupName;
        filterDropdown.appendChild(option);
    });
}

filterDropdown.addEventListener('change', () => {
    const selectedGroup = filterDropdown.value;
    if (selectedGroup) {
        renderApiGroups({[selectedGroup]: allGroups[selectedGroup]});
    } else {
        renderApiGroups(allGroups);
    }
});

function renderApiGroups(groups) {
    apiGroupsDiv.innerHTML = '';
    for (const [groupName, apis] of Object.entries(groups)) {
        const groupDiv = document.createElement('div');
        groupDiv.classList.add('group');
        groupDiv.innerHTML = `<h2>${groupName}</h2>`;
        const apiList = document.createElement('ul');
        apiList.classList.add('api-list');

        apis.forEach(api => {
            const apiItem = document.createElement('li');
            apiItem.classList.add('api-item', api.checked ? 'checked' : 'unchecked');

            const formattedText = formatApiText(api.httpMethod, api.path, api.description);

            const swaggerLink = swaggerLinks[`${api.httpMethod}_${api.path}`] || '';
            const nickname = api.nickname ? `<span class="api-nickname">[${api.nickname}]</span>` : '';

            apiItem.innerHTML = `
                <div class="api-text">${formattedText}</div>
                ${nickname}
                <a href="${swaggerBase}${swaggerLink.replace(/^\/+/, '')}" target="_blank" class="swagger-link">[Swagger]</a>
                <input type="checkbox" class="checkbox" ${api.checked ? 'checked' : ''}
                data-path="${api.path}" data-method="${api.httpMethod}">
            `;
            apiList.appendChild(apiItem);
        });

        groupDiv.appendChild(apiList);
        apiGroupsDiv.appendChild(groupDiv);
    }

    document.querySelectorAll('.checkbox').forEach(checkbox => {
        checkbox.addEventListener('change', handleCheck);
    });
}

function formatApiText(httpMethod, path, description) {
    const method = `<span class="http-method">${httpMethod}</span>`;
    const apiPath = `<span class="api-path">${path}</span>`;
    const apiDescription = `- ${description}`;
    return `${method} | ${apiPath} ${apiDescription}`;
}

async function handleCheck(event) {
    const checkbox = event.target;
    const path = checkbox.getAttribute('data-path');
    const httpMethod = checkbox.getAttribute('data-method');
    const checked = checkbox.checked;
    const nickname = getNickname();

    try {
        const queryString = new URLSearchParams({
            httpMethod: httpMethod,
            path: path,
            nickname: nickname,
            checked: checked
        }).toString();

        const response = await fetch(`${apiBase}/check?${queryString}`, {
            method: 'GET',
        });

        if (!response.ok) throw new Error('Failed to update API status');
        showMessage('API status updated successfully', 'success');

        await fetchApiGroups();
    } catch (error) {
        console.error('Error updating API status:', error);
        showMessage('Error updating API status', 'error');
        checkbox.checked = !checked;
    }
}

function showMessage(message, type) {
    messageDiv.textContent = message;
    messageDiv.className = type;
    setTimeout(() => (messageDiv.textContent = ''), 3000);
}

fetchApiGroups();
