const axios = require('axios');

// Configuration
const BACKEND_URL = process.env.BACKEND_URL || 'http://backend:8080';
const MIN_INTERVAL_MS = 3000; // 3 seconds
const MAX_INTERVAL_MS = 12000; // 12 seconds

// Sample data directly from init.sql
const USERS = [
  { id: 1, name: 'John Doe', email: 'john.doe@example.com' },
  { id: 2, name: 'Jane Smith', email: 'jane.smith@example.com' },
  { id: 3, name: 'Bob Johnson', email: 'bob.johnson@example.com' },
  { id: 4, name: 'Alice Brown', email: 'alice.brown@example.com' },
  { id: 5, name: 'Charlie Wilson', email: 'charlie.wilson@example.com' }
];

const PRODUCTS = [
  // TechCorp Electronics products
  { id: 1, name: 'Wireless Headphones Pro', price: 299.99, manufacturerId: 1 },
  { id: 2, name: 'Smart Watch Series X', price: 399.99, manufacturerId: 1 },
  { id: 3, name: 'Bluetooth Speaker Max', price: 199.99, manufacturerId: 1 },
  { id: 4, name: 'Gaming Mouse Elite', price: 89.99, manufacturerId: 1 },
  { id: 5, name: 'USB-C Hub Deluxe', price: 79.99, manufacturerId: 1 },
  
  // Global Manufacturing Inc products
  { id: 6, name: 'Steel Water Bottle', price: 24.99, manufacturerId: 2 },
  { id: 7, name: 'Ergonomic Office Chair', price: 449.99, manufacturerId: 2 },
  { id: 8, name: 'Adjustable Desk Lamp', price: 69.99, manufacturerId: 2 },
  { id: 9, name: 'Portable Phone Stand', price: 19.99, manufacturerId: 2 },
  { id: 10, name: 'Multi-Tool Kit', price: 39.99, manufacturerId: 2 },
  
  // Premium Products Ltd products
  { id: 11, name: 'Leather Wallet Premium', price: 89.99, manufacturerId: 3 },
  { id: 12, name: 'Silk Scarf Collection', price: 129.99, manufacturerId: 3 },
  { id: 13, name: 'Titanium Sunglasses', price: 349.99, manufacturerId: 3 },
  { id: 14, name: 'Cashmere Sweater', price: 299.99, manufacturerId: 3 },
  { id: 15, name: 'Designer Handbag', price: 599.99, manufacturerId: 3 },
  
  // Innovation Industries products
  { id: 16, name: 'Smart Home Hub', price: 199.99, manufacturerId: 4 },
  { id: 17, name: 'Wireless Charging Pad', price: 49.99, manufacturerId: 4 },
  { id: 18, name: 'Voice Assistant Pro', price: 149.99, manufacturerId: 4 },
  { id: 19, name: 'Security Camera System', price: 299.99, manufacturerId: 4 },
  { id: 20, name: 'Smart Doorbell', price: 179.99, manufacturerId: 4 },
  
  // Quality Solutions Co products
  { id: 21, name: 'Professional Toolkit', price: 159.99, manufacturerId: 5 },
  { id: 22, name: 'High-Quality Backpack', price: 89.99, manufacturerId: 5 },
  { id: 23, name: 'Stainless Steel Cookware', price: 249.99, manufacturerId: 5 },
  { id: 24, name: 'Precision Scale', price: 79.99, manufacturerId: 5 },
  { id: 25, name: 'Digital Thermometer', price: 29.99, manufacturerId: 5 }
];

const USER_IDS = USERS.map(user => user.id);
const PRODUCT_IDS = PRODUCTS.map(product => product.id);

class OrderBatchJob {
  constructor() {
    this.isRunning = false;
    this.orderCount = 0;
    this.timeoutId = null;
  }

  getRandomInterval() {
    return Math.random() * (MAX_INTERVAL_MS - MIN_INTERVAL_MS) + MIN_INTERVAL_MS;
  }

  // Generate a random order request
  generateRandomOrder() {
    const user = USERS[Math.floor(Math.random() * USERS.length)];
    
    // Generate 1-3 random order items
    const numItems = Math.floor(Math.random() * 3) + 1;
    const orderItems = [];
    const usedProductIds = new Set();
    
    for (let i = 0; i < numItems; i++) {
      let product;
      
      // Ensure unique products in the same order
      do {
        product = PRODUCTS[Math.floor(Math.random() * PRODUCTS.length)];
      } while (usedProductIds.has(product.id));
      
      usedProductIds.add(product.id);
      
      const quantity = Math.floor(Math.random() * 5) + 1; // 1-5 quantity
      
      orderItems.push({
        productId: product.id,
        quantity: quantity,
        // Include product details for logging
        _productName: product.name,
        _productPrice: product.price
      });
    }
    
    return {
      userId: user.id,
      orderItems: orderItems.map(item => ({
        productId: item.productId,
        quantity: item.quantity
      })),
      // Include user details for logging
      _userName: user.name,
      _userEmail: user.email,
      _orderItemsWithDetails: orderItems
    };
  }

  // Create an order via API
  async createOrder() {
    try {
      const orderData = this.generateRandomOrder();
      
      console.log(`[${new Date().toISOString()}] Creating order #${this.orderCount + 1}:`);
      console.log(`  User: ${orderData._userName} (${orderData._userEmail})`);
      console.log(`  Items:`);
      orderData._orderItemsWithDetails.forEach(item => {
        console.log(`    - ${item.quantity}x ${item._productName} @ $${item._productPrice} each`);
      });
      
      // Remove the logging fields before sending to API
      const apiPayload = {
        userId: orderData.userId,
        orderItems: orderData.orderItems
      };
      
      const response = await axios.post(`${BACKEND_URL}/orders`, apiPayload, {
        headers: {
          'Content-Type': 'application/json'
        },
        timeout: 5000 // 5 second timeout
      });
      
      this.orderCount++;
      console.log(`[${new Date().toISOString()}] ✅ Order created successfully! Order ID: ${response.data.id}, Total: $${response.data.totalAmount}`);
      
      if (this.isRunning) {
        const nextInterval = this.getRandomInterval();
        console.log(`⏰ Next order in ${Math.round(nextInterval / 1000)} seconds`);
        this.scheduleNextOrder(nextInterval);
      }
      console.log('---');
      
    } catch (error) {
      console.error(`[${new Date().toISOString()}] ❌ Error creating order:`, error.message);
      
      if (error.response) {
        console.error('Response status:', error.response.status);
        console.error('Response data:', error.response.data);
      }
      
      if (this.isRunning) {
        const nextInterval = this.getRandomInterval();
        console.log(`⏰ Next order attempt in ${Math.round(nextInterval / 1000)} seconds`);
        this.scheduleNextOrder(nextInterval);
      }
      console.log('---');
    }
  }

  scheduleNextOrder(intervalMs) {
    this.timeoutId = setTimeout(() => {
      this.createOrder();
    }, intervalMs);
  }

  // Start the batch job
  start() {
    if (this.isRunning) {
      console.log('Batch job is already running');
      return;
    }
    
    this.isRunning = true;
    console.log(`[${new Date().toISOString()}] Starting order batch job...`);
    console.log(`Backend URL: ${BACKEND_URL}`);
    console.log(`Random interval: ${MIN_INTERVAL_MS / 1000}-${MAX_INTERVAL_MS / 1000} seconds`);
    
    // Create first order immediately
    this.createOrder();
  }

  // Stop the batch job
  stop() {
    if (!this.isRunning) {
      console.log('Batch job is not running');
      return;
    }
    
    this.isRunning = false;
    if (this.timeoutId) {
      clearTimeout(this.timeoutId);
      this.timeoutId = null;
    }
    console.log(`[${new Date().toISOString()}] Batch job stopped. Total orders created: ${this.orderCount}`);
  }
}

// Handle graceful shutdown
process.on('SIGINT', () => {
  console.log('\nReceived SIGINT, shutting down gracefully...');
  if (batchJob) {
    batchJob.stop();
  }
  process.exit(0);
});

process.on('SIGTERM', () => {
  console.log('\nReceived SIGTERM, shutting down gracefully...');
  if (batchJob) {
    batchJob.stop();
  }
  process.exit(0);
});

// Start the batch job
const batchJob = new OrderBatchJob();
batchJob.start(); 