<template>
    <div>
      <b-navbar variant="dark" type="dark">
        <b-navbar-brand href="#">Deppplom</b-navbar-brand>
        <b-navbar-nav>
            <b-nav-item href="#" @click="toggleUsers()">Пользователи</b-nav-item>
            <b-nav-item href="#" @click="toggleGroups()">Группы</b-nav-item>
            <b-nav-item href="#" disabled>Тесты</b-nav-item>
        </b-navbar-nav>
        <b-nav-text class="ml-auto" right>{{ user.name }}</b-nav-text>
      </b-navbar>
      <div v-if="toggle_users">
        <users-catalogue></users-catalogue>
      </div>
      <div v-else-if="toggle_groups">
        <groups-catalogue></groups-catalogue>
      </div>
    </div>
</template>
<script>
  module.exports = {
    props: {
      user: {
          type: Object,
          required: true
      }
    },
    data() {
      return {
        toggle_users: false,
        toggle_groups: false
      };
    },
    components: {
      'users-catalogue': window.httpVueLoader('components/admin/users_catalogue.vue'),
      'groups-catalogue': window.httpVueLoader('components/admin/groups_catalogue.vue'),
    },
    methods: {
      toggleUsers() {
        this.toggle_groups = false;
        this.toggle_users = true;
      },
      toggleGroups() {
        this.toggle_users = false;
        this.toggle_groups = true;
      }
    }
  };
</script>