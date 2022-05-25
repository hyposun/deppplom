new window.Vue({
  el: '#app',
  components: {
      'home-admin': window.httpVueLoader('components/admin/home.vue'),
      'home-teacher': window.httpVueLoader('components/teacher/home.vue'),
      'home-student': window.httpVueLoader('components/student/home.vue'),
  },
  methods: {
    getUser() {
      return {
        id: 1,
        name: 'Админ Админович',
        role: "ADMIN"
      }
    }
  },
  computed: {
    role() {
      return this.getUser().role
    }
  }
})