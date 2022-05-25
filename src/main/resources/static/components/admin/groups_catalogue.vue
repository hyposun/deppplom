<template>
    <div class="mb-2 p-3">
        <h3>Группы</h3>
    </div>
</template>
<script>
  module.exports = {
    data() {
      
    },
    mounted() {

    },
    computed: {

    },
    methods: {
        async loadUsers() {
            let response = await fetch(`/api/v0/users?page=${this.page}&size=${this.size}`);
            const data = await response.json();
            if (!response.ok) {
                const error = (data && data.message) || response.statusText;
                alert("Ошибка загрузки пользователей: " + error);
                return Promise.reject(error);
            }
            this.users = data.content;
        },
    },
  };
</script>