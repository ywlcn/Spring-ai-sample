    // 获取 DOM 元素
    const sidebar = document.getElementById('sidebar');
    const sidebarOpenBtn = document.getElementById('sidebar-open-btn');
    const sidebarCloseBtn = document.getElementById('sidebar-close-btn');
    const newChatBtn = document.getElementById('new-chat-btn');
    const messagesContainer = document.getElementById('messages-container').querySelector('div');
    const messageInput = document.getElementById('message-input');
    const sendButton = document.getElementById('send-button');
    const chatForm = document.getElementById('chat-form');
    const initialMessageDiv = document.getElementById('initial-message');

    // 存储消息的数组
    let messages = [];
    let isLoading = false; // 用于控制加载状态

    // 辅助函数：滚动到底部
    function scrollToBottom() {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    // 渲染消息到 UI
    function renderMessage(messageText, sender) {
        // 移除初始消息提示
        if (initialMessageDiv) {
            initialMessageDiv.style.display = 'none';
            initialMessageDiv.id = ''; // 避免重复查找
        }

        const messageDiv = document.createElement('div');
        messageDiv.className = `flex ${sender === 'user' ? 'justify-end' : 'justify-start'}`;

        const contentDiv = document.createElement('div');
        contentDiv.className = `max-w-[75%] px-4 py-2 rounded-xl shadow-sm ${
            sender === 'user'
                ? 'bg-blue-500 text-white rounded-br-none'
                : 'bg-white text-gray-800 rounded-bl-none border border-gray-200'
        }`;
        contentDiv.textContent = messageText;

        messageDiv.appendChild(contentDiv);
        messagesContainer.appendChild(messageDiv);
        scrollToBottom();
    }

    // 显示加载动画
    function showLoading() {
        isLoading = true;
        sendButton.disabled = true;
        messageInput.disabled = true;

        const loadingDiv = document.createElement('div');
        loadingDiv.id = 'loading-indicator';
        loadingDiv.className = 'flex justify-start';
        loadingDiv.innerHTML = `
            <div class="max-w-[75%] px-4 py-2 rounded-xl shadow-sm bg-white text-gray-800 rounded-bl-none border border-gray-200">
                <div class="flex items-center">
                    <div class="dot-pulse"></div>
                </div>
            </div>
        `;
        messagesContainer.appendChild(loadingDiv);
        scrollToBottom();
    }

    // 隐藏加载动画
    function hideLoading() {
        isLoading = false;
        sendButton.disabled = messageInput.value.trim() === ''; // 根据输入框内容重新启用按钮
        messageInput.disabled = false;
        const loadingDiv = document.getElementById('loading-indicator');
        if (loadingDiv) {
            loadingDiv.remove();
        }
    }

    // 发送消息
    async function sendMessage() {
        const messageText = messageInput.value.trim();
        if (messageText === '') return;

        // 添加用户消息到 UI
        renderMessage(messageText, 'user');
        messages.push({ text: messageText, sender: 'user' }); // 存储消息历史

        messageInput.value = ''; // 清空输入框
        updateSendButtonState(); // 禁用发送按钮

        showLoading(); // 显示加载动画

        try {
            const response = await fetch('http://localhost:8080/api/chat/send', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ message: messageText }),
            });

            if (!response.ok) {
                throw new Error(`HTTP 错误! 状态码: ${response.status}`);
            }

            const data = await response.json();
            renderMessage(data.response, 'ai');
            messages.push({ text: data.response, sender: 'ai' }); // 存储消息历史

        } catch (error) {
            console.error('发送消息时出错:', error);
            renderMessage(`抱歉，发生错误: ${error.message}`, 'ai');
            messages.push({ text: `抱歉，发生错误: ${error.message}`, sender: 'ai' });
        } finally {
            hideLoading(); // 隐藏加载动画
        }
    }

    // 更新发送按钮状态
    function updateSendButtonState() {
        sendButton.disabled = messageInput.value.trim() === '' || isLoading;
    }

    // 自动调整文本框高度
    function autoResizeTextarea() {
        messageInput.style.height = 'auto'; // Reset height
        messageInput.style.height = (messageInput.scrollHeight) + 'px'; // Set to scroll height
        if (messageInput.scrollHeight > 112) { // Max height for 28 (7 lines * 16px line-height + padding)
            messageInput.style.overflowY = 'auto';
        } else {
            messageInput.style.overflowY = 'hidden';
        }
    }

    // 事件监听器
    chatForm.addEventListener('submit', function(event) {
        event.preventDefault(); // 阻止表单默认提交行为
        sendMessage();
    });

    messageInput.addEventListener('input', function() {
        updateSendButtonState();
        autoResizeTextarea();
    });

    messageInput.addEventListener('keypress', function(event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault(); // 阻止换行
            if (!sendButton.disabled) {
                sendMessage();
            }
        }
    });

    newChatBtn.addEventListener('click', function() {
        messages = []; // 清空消息数组
        messagesContainer.innerHTML = `
            <div id="initial-message" class="flex flex-col items-center justify-center h-full text-center text-gray-500 mt-20">
                <h3 class="text-2xl font-semibold mb-2">与您的 AI 助手聊天</h3>
                <p>输入消息开始对话。</p>
            </div>
        `;
        messageInput.value = '';
        updateSendButtonState();
        autoResizeTextarea(); // Reset textarea height
        // 隐藏侧边栏（如果是在移动端）
        if (window.innerWidth < 768) {
            sidebar.classList.remove('translate-x-0');
            sidebar.classList.add('-translate-x-full');
        }
    });

    sidebarOpenBtn.addEventListener('click', function() {
        sidebar.classList.remove('-translate-x-full');
        sidebar.classList.add('translate-x-0');
    });

    sidebarCloseBtn.addEventListener('click', function() {
        sidebar.classList.remove('translate-x-0');
        sidebar.classList.add('-translate-x-full');
    });

    // 初始状态更新
    updateSendButtonState();
    autoResizeTextarea();