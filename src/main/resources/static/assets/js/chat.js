let stompClient, user, activeChat=null;

function parseMessage(message) {
    if(message.type==="TEXT"){
        return `
                <div class="my-3 message-${message.from.id===user.id?'my':'from'}">
                    <div class="message rounded-lg">
                        <div class="from">${message.from.username}</div>
                        <p class="m-0 ">${message.text}</p>
                        <div class="time">${message.time.substring(message.time.indexOf("T")+1, message.time.indexOf("T")+6)}</div>
                    </div>
                    
                </div>
                `
    }else if(message.type==="JOINED"){
        return `
                <div class="my-3 message-joined">
                    <div class="message rounded-lg">
                        <p class="m-0 ">${message.text}</p>
                    </div>
                    
                </div>
                `
    }
}


function parseUpdate(payload) {
    let update=JSON.parse(payload.body)
    if(update.type==="NEW_MESSAGE"){
        console.log(update, activeChat)
        if(update.data.chat.id===activeChat.id){
            let chatBody=document.querySelector("#chat-body")
            let message=update.data
            chatBody.innerHTML=(chatBody.innerHTML+parseMessage(message))
        }
    }
}


function closeChat() {
    document.querySelector("#right").innerHTML= "<div class=\"d-flex " +
        "justify-content-center align-items-center h-100\">\n" +
        "            <h5>Nothing to show...</h5>\n" +
        "        </div>"
}
function messageChange(e){
    //TODO Message not empty check logic
}

function getCookie(cname) {
    let name = cname + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    console.log(decodedCookie)
    let ca = decodedCookie.split(';');
    console.log(ca)
    for(let i = 0; i <ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            console.log(c)
            console.log(name)
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

function sendMessage() {
    let input=document.querySelector("#message"), text=input.value
    stompClient.send("/app/chat.sendMessage", {}, JSON.stringify({
        from: user,
        text,
        type: 'TEXT',
        chat: activeChat
    }))
    input.value=""
}

async function connect() {
    let socket = new SockJS('/ws');

    stompClient = Stomp.over(socket);
    // stompClient.debug=false
    await stompClient.connect({}, async ()=>{

        let request=await fetch("/auth/main", {
            method: 'GET'
        })
        let res=await request.json()
        if(res.success===false){
            throw new Error(res.message)
        }
        user=res.data
        stompClient.subscribe("/topic/chat/open/"+user.id, async (payload)=>{
            document.querySelector("#right").innerHTML="<div class='d-flex" +
                " justify-content-center align-items-center'>" +
                "<span class='spinner-border text-primary'></span></div>"
            let response=JSON.parse(payload.body).data
            activeChat=response.chat
            let messages=""
            for (let message of response.messages) {
                messages+=parseMessage(message)
            }
            document.querySelector("#right").innerHTML=`
                <div>
                    <nav class="chat-header">
                        <div class="chat-header-title">
                            <b>${activeChat.title}</b>
                            <br/>
                            <p>${activeChat.members.length} members</p>
                        </div>
                    </nav>
                    <main class="chat-body" id="chat-body">
                        ${messages}
                    </main>
                    <footer class="message-sender">
                        <form id="message-form" class="w-100 d-flex align-items-center">
                            <input type="text" autocomplete="off" class="form-control" placeholder="Type message..." id="message" onchange="messageChange(this)">
                            <button type="submit" class="btn btn-primary ml-3" onclick="sendMessage(${activeChat.id})">Send</button>
                        </form>
                       
                    </footer>
                </div>
            `
            $("#message-form").submit(function (){
                return false
            })
        })
        stompClient.subscribe("/topic/update/"+user.id, parseUpdate)
    }, (e)=>{
        console.error(e)
        document.body.innerHTML="<div class='w-100 h-100 d-flex justify-content-center align-items-center'>" +
            "<h3>Something went wrong... Try to <a href='/auth/login' class='nav-link d-inline p-0'>login</a> again!</h3>" +
            "</div>"
    })
    stompClient.reconnect_delay = 5000;
}

function openChat(id){
    // history.replaceState({html: document.body}, "", "/chat?id="+id)
    stompClient.send("/app/chat.open", {}, JSON.stringify(id))
}

$(document).ready(function (){
    console.log('Document ready')
    connect().then(()=>{
        console.log('Connected')
        document.querySelector("#loader").classList.remove("d-flex")
        document.querySelector("#loader").classList.add("d-none")
        document.querySelector(".main").removeAttribute("hidden")
    })
})
